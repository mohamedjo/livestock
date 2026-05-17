package com.shabic.livestock.application.service;

import com.shabic.livestock.application.command.RegisterAnimalCommand;
import com.shabic.livestock.application.command.UpdateAnimalCommand;
import com.shabic.livestock.application.messaging.AnimalEventPublisher;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.domain.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

	@Mock private AnimalRepository animalRepo;
	@Mock private AnimalHistoryRepository historyRepo;
	@Mock private AnimalEventPublisher animalEventPublisher;

	@Captor private ArgumentCaptor<Animal> animalCaptor;
	@Captor private ArgumentCaptor<AnimalCreated> animalCreatedCaptor;

	private AnimalService service;

	@BeforeEach
	void setUp() {
		service = new AnimalService(animalRepo, historyRepo, animalEventPublisher);
	}

	@Test
	void register_savesNewAnimal_andReturnsId() {
		RegisterAnimalCommand cmd = minimalRegisterCommand("  TAG-1  ", null);
		when(animalRepo.findByTagNumber("TAG-1")).thenReturn(Optional.empty());

		UUID id = service.register(cmd);

		verify(animalRepo).save(animalCaptor.capture());
		Animal saved = animalCaptor.getValue();
		assertThat(id).isEqualTo(saved.getId());
		assertThat(saved.getTagNumber()).isEqualTo("TAG-1");
		assertThat(saved.getFarmId()).isEqualTo(cmd.farmId());
		assertThat(saved.getType()).isEqualTo(cmd.type());
		assertThat(saved.getCurrentLocationId()).isEqualTo(cmd.initialLocationId());
		assertThat(saved.getStatus()).isEqualTo(AnimalStatus.ALIVE);

		verify(animalEventPublisher).publishAnimalCreated(animalCreatedCaptor.capture());
		AnimalCreated published = animalCreatedCaptor.getValue();
		assertThat(published.animalId()).isEqualTo(id);
		assertThat(published.farmId()).isEqualTo(cmd.farmId());
		assertThat(published.type()).isEqualTo(cmd.type());
		assertThat(published.eventType()).isEqualTo("AnimalCreated");
		assertThat(published.timestamp()).isNotNull();
	}

	@Test
	void register_rejectsDuplicateTagNumber_afterNormalization() {
		RegisterAnimalCommand cmd = minimalRegisterCommand("  TAG-1  ", null);
		when(animalRepo.findByTagNumber("TAG-1")).thenReturn(Optional.of(existingAnimal(cmd.farmId(), UUID.randomUUID(), "TAG-1")));

		assertThatThrownBy(() -> service.register(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("tagNumber already exists");

		verify(animalRepo, never()).save(any());
		verify(animalEventPublisher, never()).publishAnimalCreated(any());
	}

	@Test
	void register_rejectsMissingMotherAnimal_whenProvided() {
		UUID motherId = UUID.randomUUID();
		RegisterAnimalCommand cmd = minimalRegisterCommand("TAG-1", motherId);
		when(animalRepo.findByTagNumber("TAG-1")).thenReturn(Optional.empty());
		when(animalRepo.findById(motherId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.register(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("mother animal not found");

		verify(animalRepo, never()).save(any());
		verify(animalEventPublisher, never()).publishAnimalCreated(any());
	}

	@Test
	void update_rejectsWhenAnimalNotFound() {
		UUID animalId = UUID.randomUUID();
		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, minimalRegisterCommand("TAG-NEW", null));
		when(animalRepo.findById(animalId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.update(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("animal not found");
	}

	@Test
	void update_rejectsFarmChange() {
		UUID animalId = UUID.randomUUID();
		UUID farmA = UUID.randomUUID();
		UUID farmB = UUID.randomUUID();

		Animal existing = existingAnimal(farmA, animalId, "TAG-1");
		when(animalRepo.findById(animalId)).thenReturn(Optional.of(existing));

		RegisterAnimalCommand details = minimalRegisterCommand("TAG-1", null, farmB);
		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, details);

		assertThatThrownBy(() -> service.update(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("farmId cannot be changed");
		verify(animalRepo, never()).save(any());
	}

	@Test
	void update_rejectsDuplicateTagNumber_ownedByAnotherAnimal() {
		UUID farmId = UUID.randomUUID();
		UUID animalId = UUID.randomUUID();
		UUID otherId = UUID.randomUUID();

		Animal existing = existingAnimal(farmId, animalId, "TAG-1");
		when(animalRepo.findById(animalId)).thenReturn(Optional.of(existing));
		when(animalRepo.findByTagNumber("TAG-2")).thenReturn(Optional.of(existingAnimal(farmId, otherId, "TAG-2")));

		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, minimalRegisterCommand("TAG-2", null, farmId));

		assertThatThrownBy(() -> service.update(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("tagNumber already exists");
		verify(animalRepo, never()).save(any());
	}

	@Test
	void update_rejectsMotherReferencingSelf() {
		UUID farmId = UUID.randomUUID();
		UUID animalId = UUID.randomUUID();

		Animal existing = existingAnimal(farmId, animalId, "TAG-1");
		when(animalRepo.findById(animalId)).thenReturn(Optional.of(existing));

		RegisterAnimalCommand details = minimalRegisterCommand("TAG-1", animalId, farmId);
		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, details);

		assertThatThrownBy(() -> service.update(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("motherAnimalId cannot reference the same animal");
		verify(animalRepo, never()).save(any());
	}

	@Test
	void update_rejectsMissingMotherAnimal_whenProvided() {
		UUID farmId = UUID.randomUUID();
		UUID animalId = UUID.randomUUID();
		UUID motherId = UUID.randomUUID();

		Animal existing = existingAnimal(farmId, animalId, "TAG-1");
		when(animalRepo.findById(animalId)).thenReturn(Optional.of(existing));
		when(animalRepo.findById(motherId)).thenReturn(Optional.empty());

		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, minimalRegisterCommand("TAG-1", motherId, farmId));

		assertThatThrownBy(() -> service.update(cmd))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("mother animal not found");
		verify(animalRepo, never()).save(any());
	}

	@Test
	void update_savesRehydratedAnimal_withNormalizedFields() {
		UUID farmId = UUID.randomUUID();
		UUID animalId = UUID.randomUUID();
		Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");

		Animal existing = Animal.rehydrate(
				animalId,
				"TAG-1",
				"Cow",
				"Breed",
				Gender.FEMALE,
				LocalDate.parse("2020-01-01"),
				farmId,
				null,
				null,
				null,
				null,
				null,
				Set.of("grass"),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				UUID.randomUUID(),
				AnimalStatus.ALIVE,
				createdAt
		);
		when(animalRepo.findById(animalId)).thenReturn(Optional.of(existing));
		when(animalRepo.findByTagNumber("TAG-2")).thenReturn(Optional.empty());

		RegisterAnimalCommand details = new RegisterAnimalCommand(
				"  TAG-2  ",
				"  Sheep ",
				"  Merino ",
				"female",
				LocalDate.parse("2021-05-05"),
				farmId,
				UUID.randomUUID(),
				null,
				null,
				null,
				LocalDate.parse("2022-02-02"),
				null,
				List.of(" hay ", "hay"),
				"  kw ",
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		);
		UpdateAnimalCommand cmd = new UpdateAnimalCommand(animalId, details);

		Animal updated = service.update(cmd);

		verify(animalRepo).save(animalCaptor.capture());
		Animal saved = animalCaptor.getValue();
		assertThat(updated).isEqualTo(saved);
		assertThat(saved.getId()).isEqualTo(animalId);
		assertThat(saved.getFarmId()).isEqualTo(farmId);
		assertThat(saved.getTagNumber()).isEqualTo("TAG-2");
		assertThat(saved.getType()).isEqualTo("Sheep");
		assertThat(saved.getBreed()).isEqualTo("Merino");
		assertThat(saved.getGender()).isEqualTo(Gender.FEMALE);
		assertThat(saved.getFeedTypes()).containsExactlyInAnyOrder("hay", " hay ");
		assertThat(saved.getCurrentLocationId()).isEqualTo(details.initialLocationId());
		assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
	}

	@Test
	void getDetails_returnsAnimal_orThrows() {
		UUID id = UUID.randomUUID();
		Animal existing = existingAnimal(UUID.randomUUID(), id, "TAG-1");
		when(animalRepo.findById(id)).thenReturn(Optional.of(existing));

		assertThat(service.getDetails(id)).isSameAs(existing);

		when(animalRepo.findById(id)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.getDetails(id))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("animal not found");
	}

	@Test
	void getByFarm_delegatesToRepository() {
		UUID farmId = UUID.randomUUID();
		List<Animal> animals = List.of(existingAnimal(farmId, UUID.randomUUID(), "T1"));
		when(animalRepo.findByFarmId(farmId)).thenReturn(animals);

		assertThat(service.getByFarm(farmId)).isSameAs(animals);
	}

	@Test
	void history_delegatesToRepository() {
		UUID animalId = UUID.randomUUID();
		List<AnimalHistoryRecord> records = List.of(
				new AnimalHistoryRecord(UUID.randomUUID(), animalId, "AnimalFed", "{}", Instant.now())
		);
		when(historyRepo.findByAnimalId(animalId)).thenReturn(records);

		assertThat(service.history(animalId)).isSameAs(records);
	}

	private static RegisterAnimalCommand minimalRegisterCommand(String tagNumber, UUID motherAnimalId) {
		return minimalRegisterCommand(tagNumber, motherAnimalId, UUID.randomUUID());
	}

	private static RegisterAnimalCommand minimalRegisterCommand(String tagNumber, UUID motherAnimalId, UUID farmId) {
		return new RegisterAnimalCommand(
				tagNumber,
				"Cow",
				null,
				null,
				null,
				farmId,
				UUID.randomUUID(),
				motherAnimalId,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		);
	}

	private static Animal existingAnimal(UUID farmId, UUID animalId, String tagNumber) {
		return Animal.rehydrate(
				animalId,
				tagNumber,
				"Cow",
				null,
				Gender.UNKNOWN,
				null,
				farmId,
				null,
				null,
				null,
				null,
				null,
				Set.of(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				UUID.randomUUID(),
				AnimalStatus.ALIVE,
				Instant.parse("2024-01-01T00:00:00Z")
		);
	}
}
