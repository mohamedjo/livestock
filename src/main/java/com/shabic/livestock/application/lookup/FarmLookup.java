package com.shabic.livestock.application.lookup;

import java.util.UUID;

/**
 * Cross-service lookup for farms owned by the farm service.
 * Livestock only references farms by id and uses this to assert existence.
 */
public interface FarmLookup {
	void assertFarmExists(UUID farmId);
}
