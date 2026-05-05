package com.shabic.livestock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "livestock.openapi")
public record OpenApiDocProperties(
		String title,
		String version,
		String description,
		String contactName,
		String serverUrl,
		String serverDescription,
		String tagAnimalsName,
		String tagAnimalsDescription,
		String exampleRegisterFullName,
		String exampleRegisterMinimalName,
		String exampleUpdateName,
		String exampleMoveName,
		String responseAnimalIdExample
) {
}

