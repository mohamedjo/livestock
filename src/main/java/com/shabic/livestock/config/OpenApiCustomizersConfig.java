package com.shabic.livestock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
public class OpenApiCustomizersConfig {

	@Bean
	public OpenApiCustomizer animalApiDocsCustomiser(
			OpenApiDocProperties props,
			@org.springframework.beans.factory.annotation.Value("classpath:api-examples/register_or_update_animal.json") Resource registerOrUpdateExample,
			@org.springframework.beans.factory.annotation.Value("classpath:api-examples/register_minimal.json") Resource registerMinimalExample
	) {
		String registerOrUpdateJson = readUtf8(registerOrUpdateExample);
		String registerMinimalJson = readUtf8(registerMinimalExample);

		return openApi -> {
			addAnimalTag(openApi, props);
			customizeAnimalsPost(openApi, props, registerOrUpdateJson, registerMinimalJson);
			customizeAnimalsPut(openApi, props, registerOrUpdateJson);
			customizeAnimalsGetByFarm(openApi, props);
			customizeAnimalsGetById(openApi, props);
			customizeAnimalsHistory(openApi, props);
		};
	}

	private static String readUtf8(Resource resource) {
		try (var in = resource.getInputStream()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read OpenAPI example: " + resource.getDescription(), e);
		}
	}

	private static void addAnimalTag(OpenAPI openApi, OpenApiDocProperties props) {
		if (openApi.getTags() != null && openApi.getTags().stream().anyMatch(t -> props.tagAnimalsName().equals(t.getName()))) {
			return;
		}
		openApi.addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
				.name(props.tagAnimalsName())
				.description(props.tagAnimalsDescription()));
	}

	private static void customizeAnimalsPost(OpenAPI openApi, OpenApiDocProperties props, String registerOrUpdateJson, String registerMinimalJson) {
		var path = openApi.getPaths() == null ? null : openApi.getPaths().get("/api/animals");
		if (path == null || path.getPost() == null) return;

		path.getPost().setTags(java.util.List.of(props.tagAnimalsName()));
		path.getPost().setSummary("Register animal");
		path.getPost().setDescription("Creates a new animal. Response body is the generated UUID (register id).");

		ensureJsonRequestExamples(path.getPost().getRequestBody(),
				Map.of(
						props.exampleRegisterFullName(), example(registerOrUpdateJson),
						props.exampleRegisterMinimalName(), example(registerMinimalJson)
				));

		ApiResponses responses = ensureResponses(path.getPost());
		responses.addApiResponse("201", new ApiResponse().description("Animal created")
				.content(new io.swagger.v3.oas.models.media.Content()
						.addMediaType("application/json", new MediaType().schema(new StringSchema().example(props.responseAnimalIdExample())))));
		responses.addApiResponse("400", new ApiResponse().description("Validation error or duplicate tag number"));
	}

	private static void customizeAnimalsPut(OpenAPI openApi, OpenApiDocProperties props, String registerOrUpdateJson) {
		var path = openApi.getPaths() == null ? null : openApi.getPaths().get("/api/animals/{id}");
		if (path == null || path.getPut() == null) return;

		path.getPut().setTags(java.util.List.of(props.tagAnimalsName()));
		path.getPut().setSummary("Update animal");
		path.getPut().setDescription("Full replacement of editable fields. `farmId` must match the animal's current farm. `id` and `createdAt` are preserved.");

		ensureJsonRequestExamples(path.getPut().getRequestBody(),
				Map.of(props.exampleUpdateName(), example(registerOrUpdateJson)));

		ApiResponses responses = ensureResponses(path.getPut());
		responses.addApiResponse("200", new ApiResponse().description("Updated animal"));
		responses.addApiResponse("400", new ApiResponse().description("Validation error, farm mismatch, duplicate tag, or invalid mother"));
	}

	private static void customizeAnimalsGetByFarm(OpenAPI openApi, OpenApiDocProperties props) {
		var path = openApi.getPaths() == null ? null : openApi.getPaths().get("/api/animals");
		if (path == null || path.getGet() == null) return;

		path.getGet().setTags(java.util.List.of(props.tagAnimalsName()));
		path.getGet().setSummary("List animals by farm");
		path.getGet().setDescription("Returns all animals for the given `farmId`.");
	}

	private static void customizeAnimalsGetById(OpenAPI openApi, OpenApiDocProperties props) {
		var path = openApi.getPaths() == null ? null : openApi.getPaths().get("/api/animals/{id}");
		if (path == null || path.getGet() == null) return;

		path.getGet().setTags(java.util.List.of(props.tagAnimalsName()));
		path.getGet().setSummary("Get animal by id");
	}

	private static void customizeAnimalsHistory(OpenAPI openApi, OpenApiDocProperties props) {
		var path = openApi.getPaths() == null ? null : openApi.getPaths().get("/api/animals/{id}/history");
		if (path == null || path.getGet() == null) return;

		path.getGet().setTags(java.util.List.of(props.tagAnimalsName()));
		path.getGet().setSummary("Animal event history");
		path.getGet().setDescription("Domain and external events stored for this animal.");
	}

	private static ApiResponses ensureResponses(io.swagger.v3.oas.models.Operation operation) {
		if (operation.getResponses() == null) {
			operation.setResponses(new ApiResponses());
		}
		return operation.getResponses();
	}

	private static void ensureJsonRequestExamples(RequestBody requestBody, Map<String, Example> examples) {
		if (requestBody == null || requestBody.getContent() == null) return;
		MediaType json = requestBody.getContent().get("application/json");
		if (json == null) return;
		if (json.getExamples() == null) {
			json.setExamples(new java.util.LinkedHashMap<>());
		}
		json.getExamples().putAll(examples);
	}

	private static Example example(String value) {
		return new Example().value(value);
	}
}

