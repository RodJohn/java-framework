package com.bmo.ibackend.persistence.javagen;

import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SQLTypeToJavaTypeMapper {
	private static Map<String, String> mapper = Maps.newConcurrentMap();
	static {
		mapper.put("CHARACTER", "String");
		mapper.put("VARCHAR", "String");
		mapper.put("LONGVARCHAR", "String");
		mapper.put("NUMERIC", "java.math.BigDecimal");
		mapper.put("DECIMAL", "java.math.BigDecimal");
		mapper.put("BIT", "Boolean");
		mapper.put("INT", "Integer");
		mapper.put("TINYINT", "Integer");
		mapper.put("SMALLINT", "Integer");
		mapper.put("INTEGER", "Integer");
		mapper.put("BIGINT", "Long");
		mapper.put("REAL", "Float");
		mapper.put("FLOAT", "Double");
		mapper.put("DOUBLE PRECISION", "Double");
		mapper.put("BINARY", "byte[]");
		mapper.put("VARBINARY", "byte[]");
		mapper.put("LONGVARBINARY", "byte[]");
		mapper.put("DATE", "java.time.LocalDate");
		mapper.put("TIME", "java.time.LocalTime");
		mapper.put("TIMESTAMP", "java.time.LocalDateTime");
		mapper.put("DATETIME", "java.time.LocalDateTime");
		mapper.put("TEXT", "String");
		mapper.put("JSON", "String");
		mapper.put("LONGTEXT", "String");
	}

	public static String map(String sqlType, String tableName, String column) {
		String javaType = mapper.get(sqlType.toUpperCase());
		Assert.notNull(javaType, "Not Supported Yet! Table: " + tableName + ", column: " + column + ", sql type: " + sqlType);
		return javaType;
	}
}
