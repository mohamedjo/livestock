package com.shabic.livestock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Maps Keycloak {@code realm_access.roles} and {@code resource_access.{clientId}.roles}
 * to Spring {@code GrantedAuthority} values with a {@code ROLE_} prefix for use with
 * {@code @PreAuthorize("hasRole('MY_ROLE')")}.
 */
@Component
public class KeycloakJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	private final String resourceServerClientId;

	public KeycloakJwtAuthoritiesConverter(
			@Value("${livestock.security.keycloak.client-id:livestock-service}") String resourceServerClientId) {
		this.resourceServerClientId = resourceServerClientId;
	}

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		List<GrantedAuthority> result = new ArrayList<>();
		addRealmRoles(jwt, result);
		addClientRoles(jwt, result);
		return result;
	}

	private static void addRealmRoles(Jwt jwt, List<GrantedAuthority> out) {
		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		if (realmAccess == null) {
			return;
		}
		for (String role : roles(realmAccess.get("roles"))) {
			out.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
	}

	private void addClientRoles(Jwt jwt, List<GrantedAuthority> out) {
		Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
		if (resourceAccess == null) {
			return;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> client = (Map<String, Object>) resourceAccess.get(resourceServerClientId);
		if (client == null) {
			return;
		}
		for (String role : roles(client.get("roles"))) {
			out.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
	}

	private static List<String> roles(Object claim) {
		if (!(claim instanceof List<?> raw)) {
			return Collections.emptyList();
		}
		List<String> roles = new ArrayList<>();
		for (Object o : raw) {
			if (o instanceof String s && !s.isBlank()) {
				roles.add(s);
			}
		}
		return roles;
	}
}
