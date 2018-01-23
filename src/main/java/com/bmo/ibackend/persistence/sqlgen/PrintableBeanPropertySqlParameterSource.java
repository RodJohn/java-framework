package com.bmo.ibackend.persistence.sqlgen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

public class PrintableBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource {
	private Object value;

	public PrintableBeanPropertySqlParameterSource(Object object) {
		super(object);
		this.value = object;
	}

	@Override
	public String toString() {
		Field[] fields = this.value.getClass().getDeclaredFields();
		Map<String, Object> map = new LinkedHashMap<>();
		for (Field field : fields) {
			String name = field.getName();
			if (this.hasValue(name) && this.getValue(name) != null) {
				map.put(name, this.getValue(name));
			}
		}

		return map.toString();
	}

}
