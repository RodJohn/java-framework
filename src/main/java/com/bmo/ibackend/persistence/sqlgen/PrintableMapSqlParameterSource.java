package com.bmo.ibackend.persistence.sqlgen;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class PrintableMapSqlParameterSource extends MapSqlParameterSource {

	public PrintableMapSqlParameterSource() {
		super();
	}

	public PrintableMapSqlParameterSource(Map<String, ?> values) {
		super(values);
	}

	public PrintableMapSqlParameterSource(String paramName, Object value) {
		super(paramName, value);
	}

	@Override
	public String toString() {
		return this.getValues().toString();
	}

}
