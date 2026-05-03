package com.shabic.livestock.domain.model.valueobject;

import java.util.Locale;

public enum AnimalStatus {
	ALIVE,
	SOLD,
	SLAUGHTERED,
	DEAD;

	/**
	 * Maps UI values such as {@code Active} to {@link #ALIVE}. Defaults to {@link #ALIVE} when null/blank.
	 */
	public static AnimalStatus parseInitialStatus(String raw) {
		if (raw == null || raw.isBlank()) {
			return ALIVE;
		}
		String u = raw.trim().toUpperCase(Locale.ROOT);
		if ("ACTIVE".equals(u)) {
			return ALIVE;
		}
		try {
			return AnimalStatus.valueOf(u);
		} catch (Exception e) {
			return ALIVE;
		}
	}
}

