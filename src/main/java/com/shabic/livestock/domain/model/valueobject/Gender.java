package com.shabic.livestock.domain.model.valueobject;

import java.util.Locale;

public enum Gender {
	MALE,
	FEMALE,
	UNKNOWN;

	public static Gender fromNullableString(String raw) {
		if (raw == null || raw.isBlank()) return UNKNOWN;
		try {
			return Gender.valueOf(raw.trim().toUpperCase(Locale.ROOT));
		} catch (Exception e) {
			return UNKNOWN;
		}
	}
}

