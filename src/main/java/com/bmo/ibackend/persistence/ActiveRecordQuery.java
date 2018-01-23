package com.bmo.ibackend.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bmo.ibackend.AppConfig.ContextHolder;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator;
import com.bmo.ibackend.util.SQLLogger;
import com.bmo.ibackend.util.ScopeMaker;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ActiveRecordQuery<T> {
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Data
	@Accessors(fluent = true, chain = true)
	public static class OrderBy {
		@NonNull
		String column;
		boolean desc = false;
	}

	String sqlWhere;
	Object[] parameters;
	ModelRegister.MetaData<T> metaData;
	SQLGenerator sqlgen;
	JdbcTemplate jdbcTemplate = ContextHolder.getContext().getBean(JdbcTemplate.class);
	@Setter
	@Accessors(fluent = true, chain = true)
	Integer limit, offset;
	List<OrderBy> orderBy = new ArrayList<>();

	public ActiveRecordQuery(SQLGenerator sqlgen, ModelRegister.MetaData<T> metaData, String sqlWhere, Object[] parameters) {
		this.sqlgen = sqlgen;
		this.sqlWhere = sqlWhere;
		this.parameters = parameters;
		this.metaData = metaData;
	}

	public ActiveRecordQuery(SQLGenerator sqlgen, ModelRegister.MetaData<T> metaData, T sample) {
		this.sqlgen = sqlgen;
		this.metaData = metaData;
		BeanMap bean = BeanMap.create(sample);
		StringBuilder sqlWhereSb = new StringBuilder();
		List<Object> params = Lists.newArrayList();
		metaData.fieldToColumnMapping().forEach((field, column) -> {
			Object val = bean.get(field);
			if (val != null) {
				sqlWhereSb.append(column).append("=? and ");
				params.add(val);
			}
		});
		sqlWhereSb.delete(sqlWhereSb.length() - 4, sqlWhereSb.length());
		this.sqlWhere = sqlWhereSb.toString();
		this.parameters = params.toArray();
	}

	public T first() {
		limit = 1;
		offset = null;
		List<T> resultList = all();
		T first = resultList.size() > 0 ? resultList.get(0) : null;
		if (log.isDebugEnabled()) {
			log.debug("Query Result: {}", first);
		}
		return first;
	}

	public List<T> all() {
		String sql = generateSQL();
		SQLLogger.log(sql, this.parameters);
		List<T> resultList = jdbcTemplate.query(sql, this.parameters, ModelRegister.getJDBCRowMapper(metaData.modelClass()));
		if (log.isDebugEnabled()) {
			log.debug("Query Result: {}", resultList);
		}
		return resultList;
	}

	public ActiveRecordQuery<T> orderByASC(String... fields) {
		Arrays.stream(fields).forEach(field -> {
			orderBy.add(new OrderBy(field));
		});
		return this;
	}

	public ActiveRecordQuery<T> orderByDESC(String... fields) {
		Arrays.stream(fields).forEach(field -> {
			orderBy.add(new OrderBy(field, true));
		});
		return this;
	}

	public int count() {
		String sql = sqlgen.generateCount(generateSQL());
		SQLLogger.log(sql, this.parameters);
		Integer count = jdbcTemplate.queryForObject(sql, this.parameters, Integer.class);
		if (log.isDebugEnabled()) {
			log.debug("Records Count: {}", count);
		}
		return count;
	}

	protected String generateSQL() {
		String columns = metaData.columnsStr();
		String table = metaData.table();
		String where = this.sqlWhere;
		String elSql = sqlgen.generateSelect(columns, table, where, orderBy, limit, offset);

		return sqlgen.evalELSql(metaData, elSql, ScopeMaker.make(metaData, parameters));
	}
}
