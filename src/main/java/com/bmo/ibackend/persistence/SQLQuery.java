package com.bmo.ibackend.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.bmo.ibackend.AppConfig.ContextHolder;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator;
import com.bmo.ibackend.util.JDBCRowMapperFactory;
import com.bmo.ibackend.util.SQLLogger;
import com.bmo.ibackend.util.ScopeMaker;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SQLQuery<T> {
	Class<T> resultClass;
	String sql;
	Object[] parameters;

	JdbcTemplate jdbcTemplate = ContextHolder.getContext().getBean(JdbcTemplate.class);
	SQLGenerator sqlgen = ContextHolder.getContext().getBean(SQLGenerator.class);

	private SQLQuery(Class<T> resultClass, String sql, Object[] parameters) {
		super();
		this.resultClass = resultClass;
		this.sql = sql;
		this.parameters = parameters;
	}

	public static <V> SQLQuery<V> sql(Class<V> resultClass, String sql, Object... params) {
		Assert.notNull(resultClass, "result class can not be null.");
		Assert.hasText(sql, "Must provide SQL.");
		return new SQLQuery<>(resultClass, sql, params);
	}

	public static <V> SQLQuery<V> sql(String sql, Object... params) {
		Assert.hasText(sql, "Must provide SQL.");
		return new SQLQuery<>(null, sql, params);
	}

	public T first() {
		List<T> resultList = all();
		T first = resultList.size() > 0 ? resultList.get(0) : null;
		if (log.isDebugEnabled()) {
			log.debug("Query Result: {}", first);
		}
		return first;
	}

	public List<T> all() {
		Map<String, Object> scope = ScopeMaker.make(null, parameters);
		String newSql = sqlgen.evalELSql(null, this.sql, scope);
		SQLLogger.logNative(newSql, this.parameters);
		List<T> resultList = jdbcTemplate.query(newSql, this.parameters, JDBCRowMapperFactory.getJDBCRowMapper(this.resultClass));
		if (log.isDebugEnabled()) {
			log.debug("Query Result: {}", resultList);
		}
		return resultList;
	}

	public int update() {
		Map<String, Object> scope = ScopeMaker.make(null, parameters);
		String newSql = sqlgen.evalELSql(null, this.sql, scope);
		SQLLogger.logNative(newSql, this.parameters);
		int n = jdbcTemplate.update(newSql, this.parameters);
		if (log.isDebugEnabled()) {
			log.debug("Query Result: {}", n);
		}
		return n;
	}

	public void exec() {
		Map<String, Object> scope = ScopeMaker.make(null, parameters);
		String newSql = sqlgen.evalELSql(null, this.sql, scope);
		SQLLogger.logNative(newSql, this.parameters);
		jdbcTemplate.execute(newSql);
	}
}
