package com.shabic.livestock.domain.model.valueobject;

import java.util.Locale;

public enum MethodAcquired {
	RAISED_ON_FARM,
	PURCHASED,
	GIFT,
	TRANSFER,
	OTHER;

	public static MethodAcquired fromNullableString(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String normalized = raw.trim()
				.toUpperCase(Locale.ROOT)
				.replace(' ', '_')
				.replace('-', '_');
		try {
			return MethodAcquired.valueOf(normalized);
		} catch (Exception e) {
			return OTHER;
		}
	}
}
