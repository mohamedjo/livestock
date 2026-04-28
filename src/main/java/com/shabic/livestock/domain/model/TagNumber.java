package com.shabic.livestock.domain.model;

import java.util.Objects;

public final class TagNumber {
	private final String value;

	public TagNumber(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("tagNumber is required");
		}
		this.value = value.trim();
	}

	public String value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagNumber tagNumber = (TagNumber) o;
		return Objects.equals(value, tagNumber.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
