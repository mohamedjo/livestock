package com.shabic.livestock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(OpenApiDocProperties.class)
public class OpenApiConfig {

	@Bean
	public OpenAPI livestockOpenApi(OpenApiDocProperties props) {
		return new OpenAPI()
				.info(new Info()
						.title(props.title())
						.version(props.version())
						.description(props.description())
						.contact(new Contact().name(props.contactName())))
				.servers(List.of(
						new Server().url(props.serverUrl()).description(props.serverDescription())));
	}
}
