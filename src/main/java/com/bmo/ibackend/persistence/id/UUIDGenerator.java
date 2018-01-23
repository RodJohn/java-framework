package com.bmo.ibackend.persistence.id;

import java.util.UUID;

public class UUIDGenerator implements IDGenerator {

	@Override
	public Object generateId(Object model) {
		return UUID.randomUUID().toString();
	}

}
