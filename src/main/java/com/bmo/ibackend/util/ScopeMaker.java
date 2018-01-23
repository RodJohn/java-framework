package com.bmo.ibackend.util;

import java.util.HashMap;
import java.util.Map;

import com.bmo.ibackend.persistence.ModelRegister.MetaData;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ScopeMaker {
	public static Map<String, Object> make(MetaData<?> metaData, Object[] parameters) {
		Map<String, Object> scopes = new HashMap<>();
		if (metaData != null)
			scopes.putAll(metaData.fieldToColumnMapping());
		scopes.put("params", parameters);
		return scopes;
	}

}
