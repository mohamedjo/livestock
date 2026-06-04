package com.shabic.livestock.support;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

/**
 * Builds JWT tokens for integration tests. Roles are mapped the same way as
 * {@link com.shabic.livestock.config.KeycloakJwtAuthoritiesConverter} (realm_access.roles).
 */
public final class IntegrationTestJwtSupport {

	private IntegrationTestJwtSupport() {
	}

	public static Jwt jwtWithRealmRoles(String... roles) {
		Map<String, Object> realmAccess = Map.of("roles", List.of(roles));
		return Jwt.withTokenValue("integration-test-token")
				.header("alg", "none")
				.subject("integration-test-user")
				.claim("realm_access", realmAccess)
				.build();
	}

	public static String bearerToken() {
		return "Bearer integration-test-token";
	}
}
