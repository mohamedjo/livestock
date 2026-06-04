package com.shabic.livestock.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.application.lookup.FarmLookup;
import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalHistoryJpaRepository;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import com.shabic.livestock.support.IntegrationTestJwtSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllerIntegrationTest {

	private static final String ANIMALS_API = "/api/animals";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AnimalJpaRepository animalJpaRepository;

	@Autowired
	private AnimalHistoryJpaRepository animalHistoryJpaRepository;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@MockitoBean
	private FarmLookup farmLookup;

	private UUID farmId;

	@BeforeEach
	void setUp() {
		animalJpaRepository.deleteAll();
		animalHistoryJpaRepository.deleteAll();

		farmId = UUID.randomUUID();
		when(jwtDecoder.decode(any())).thenReturn(IntegrationTestJwtSupport.jwtWithRealmRoles("FARM_USER"));
		doNothing().when(farmLookup).assertFarmExists(any());
	}

	@Test
	void register_returnsCreatedAnimalId() throws Exception {
		RegisterAnimalRequest request = minimalRegisterRequest("TAG-100");

		MvcResult result = mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();

		UUID animalId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);

		assertThat(animalId).isNotNull();
		assertThat(animalJpaRepository.findById(animalId)).isPresent();
		assertThat(animalJpaRepository.findByTagNumber("TAG-100")).isPresent();
	}

	@Test
	void register_withoutAuthentication_returnsUnauthorized() throws Exception {
		RegisterAnimalRequest request = minimalRegisterRequest(null);

		mockMvc.perform(post(ANIMALS_API)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void register_withoutRequiredRole_returnsForbidden() throws Exception {
		when(jwtDecoder.decode(any())).thenReturn(IntegrationTestJwtSupport.jwtWithRealmRoles("ADMIN"));

		RegisterAnimalRequest request = minimalRegisterRequest(null);

		mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}

	@Test
	void register_withInvalidBody_returnsValidationErrors() throws Exception {
		RegisterAnimalRequest request = RegisterAnimalRequest.builder()
				.farmId(farmId)
				.build();

		mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").exists());
	}

	@Test
	void register_withDuplicateTagNumber_returnsBadRequest() throws Exception {
		RegisterAnimalRequest first = minimalRegisterRequest("DUPE-TAG");
		RegisterAnimalRequest second = minimalRegisterRequest("DUPE-TAG");

		mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(first)))
				.andExpect(status().isCreated());

		mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(second)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message").value("tagNumber already exists"));
	}

	@Test
	void getByFarm_returnsAnimalsForFarm() throws Exception {
		UUID animalId = registerAnimal(minimalRegisterRequest("FARM-A-1"));
		registerAnimal(minimalRegisterRequest("FARM-A-2"));
		registerAnimal(minimalRegisterRequest("OTHER-FARM", UUID.randomUUID()));

		MvcResult result = mockMvc.perform(get(ANIMALS_API)
						.param("farmId", farmId.toString())
						.header("Authorization", IntegrationTestJwtSupport.bearerToken()))
				.andExpect(status().isOk())
				.andReturn();

		List<AnimalResponse> animals = objectMapper.readValue(
				result.getResponse().getContentAsString(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, AnimalResponse.class)
		);

		assertThat(animals).hasSize(2);
		assertThat(animals).extracting(AnimalResponse::getId).contains(animalId);
		assertThat(animals).allMatch(a -> farmId.equals(a.getFarmId()));
	}

	@Test
	void getDetails_returnsAnimal() throws Exception {
		UUID animalId = registerAnimal(minimalRegisterRequest("DETAIL-TAG"));

		MvcResult result = mockMvc.perform(get(ANIMALS_API + "/{id}", animalId)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken()))
				.andExpect(status().isOk())
				.andReturn();

		AnimalResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalResponse.class);

		assertThat(response.getId()).isEqualTo(animalId);
		assertThat(response.getTagNumber()).isEqualTo("DETAIL-TAG");
		assertThat(response.getType()).isEqualTo("Cow");
		assertThat(response.getFarmId()).isEqualTo(farmId);
		assertThat(response.getStatus()).isEqualTo(AnimalStatus.ALIVE);
	}

	@Test
	void getDetails_whenAnimalMissing_returnsBadRequest() throws Exception {
		UUID missingId = UUID.randomUUID();

		mockMvc.perform(get(ANIMALS_API + "/{id}", missingId)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("animal not found"));
	}

	@Test
	void update_modifiesExistingAnimal() throws Exception {
		UUID animalId = registerAnimal(minimalRegisterRequest("OLD-TAG"));

		RegisterAnimalRequest updateRequest = RegisterAnimalRequest.builder()
				.tagNumber("NEW-TAG")
				.type("Sheep")
				.breed("Merino")
				.gender("female")
				.birthDate(LocalDate.parse("2021-05-05"))
				.farmId(farmId)
				.build();

		MvcResult result = mockMvc.perform(put(ANIMALS_API + "/{id}", animalId)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andReturn();

		AnimalResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalResponse.class);

		assertThat(response.getId()).isEqualTo(animalId);
		assertThat(response.getTagNumber()).isEqualTo("NEW-TAG");
		assertThat(response.getType()).isEqualTo("Sheep");
		assertThat(response.getBreed()).isEqualTo("Merino");
		assertThat(response.getGender()).isEqualTo(Gender.FEMALE);
		assertThat(animalJpaRepository.findByTagNumber("NEW-TAG")).isPresent();
	}

	@Test
	void history_returnsRecordsForAnimal() throws Exception {
		UUID animalId = registerAnimal(minimalRegisterRequest("HIST-TAG"));

		AnimalHistoryEntity record = AnimalHistoryEntity.builder()
				.id(UUID.randomUUID())
				.animalId(animalId)
				.eventType("AnimalFed")
				.eventData("{}")
				.createdAt(Instant.parse("2024-06-01T10:00:00Z"))
				.build();
		animalHistoryJpaRepository.save(record);

		MvcResult result = mockMvc.perform(get(ANIMALS_API + "/{id}/history", animalId)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken()))
				.andExpect(status().isOk())
				.andReturn();

		List<AnimalHistoryResponse> history = objectMapper.readValue(
				result.getResponse().getContentAsString(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, AnimalHistoryResponse.class)
		);

		assertThat(history).hasSize(1);
		assertThat(history.get(0).getAnimalId()).isEqualTo(animalId);
		assertThat(history.get(0).getEventType()).isEqualTo("AnimalFed");
	}

	private RegisterAnimalRequest minimalRegisterRequest(String tagNumber) {
		return minimalRegisterRequest(tagNumber, farmId);
	}

	private RegisterAnimalRequest minimalRegisterRequest(String tagNumber, UUID targetFarmId) {
		return RegisterAnimalRequest.builder()
				.tagNumber(tagNumber)
				.type("Cow")
				.farmId(targetFarmId)
				.build();
	}

	private UUID registerAnimal(RegisterAnimalRequest request) throws Exception {
		MvcResult result = mockMvc.perform(post(ANIMALS_API)
						.header("Authorization", IntegrationTestJwtSupport.bearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();

		return objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
	}
}
