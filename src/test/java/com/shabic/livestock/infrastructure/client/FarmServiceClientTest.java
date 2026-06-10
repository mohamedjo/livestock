package com.shabic.livestock.infrastructure.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class FarmServiceClientTest {

	private MockRestServiceServer server;
	private FarmServiceClient client;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		client = new FarmServiceClient(builder, "http://localhost:8081", "");
	}

	@AfterEach
	void verifyServer() {
		server.verify();
	}

	@Test
	void assertFarmExists_maps404ToFarmNotFound() {
		UUID farmId = UUID.randomUUID();
		server.expect(requestTo("http://localhost:8081/api/farms/" + farmId))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.body("{\"code\":\"NOT_FOUND\",\"message\":\"farm not found\"}")
						.contentType(MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> client.assertFarmExists(farmId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("farm not found");
	}

	@Test
	void assertFarmExists_mapsLegacy400FarmNotFoundBodyToFarmNotFound() {
		UUID farmId = UUID.randomUUID();
		server.expect(requestTo("http://localhost:8081/api/farms/" + farmId))
				.andRespond(withStatus(HttpStatus.BAD_REQUEST)
						.body("{\"code\":\"BAD_REQUEST\",\"message\":\"farm not found\"}")
						.contentType(MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> client.assertFarmExists(farmId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("farm not found");
	}

	@Test
	void assertFarmExists_mapsOther4xxToBadRequestStyleError() {
		UUID farmId = UUID.randomUUID();
		server.expect(requestTo("http://localhost:8081/api/farms/" + farmId))
				.andRespond(withStatus(HttpStatus.BAD_REQUEST)
						.body("{\"code\":\"BAD_REQUEST\",\"message\":\"invalid request\"}")
						.contentType(MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> client.assertFarmExists(farmId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("farm service rejected request: 400 BAD_REQUEST");
	}
}
