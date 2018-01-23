package com.bmo.ibackend.persistence;

import org.springframework.jdbc.core.RowMapper;

public interface JDBCRowMapper<T> extends RowMapper<T> {
	void setMappedClass(Class<T> mappedClass);
}
