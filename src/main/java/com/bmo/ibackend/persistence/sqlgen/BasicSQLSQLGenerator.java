package com.bmo.ibackend.persistence.sqlgen;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.bmo.ibackend.persistence.ActiveRecordQuery.OrderBy;
import com.bmo.ibackend.persistence.ModelRegister;
import com.bmo.ibackend.persistence.ModelRegister.MetaData;
import com.google.common.collect.Maps;

import lombok.SneakyThrows;
import net.sf.cglib.beans.BeanMap;

public abstract class BasicSQLSQLGenerator implements SQLGenerator {

	@Override
	public <T> SQLInfo generateInsert(ModelRegister.MetaData<T> meta, T model) {
		StringBuilder sqlSb = new StringBuilder("INSERT INTO ");
		sqlSb.append(prepareTable(meta));

		SQLColumnsInfo info = prepareColumns(meta, model);
		sqlSb.append('(').append(info.columns());
		sqlSb.append(") VALUES (");
		sqlSb.append(info.placeholders());
		sqlSb.append(")");
		String sql = sqlSb.toString();

		return new SQLInfo().sql(sql).parameter(info.parameter());
	}

	@Override
	public <T> SQLInfo generateUpdate(ModelRegister.MetaData<T> meta, T model) {
		StringBuilder sqlSb = new StringBuilder("UPDATE ");
		sqlSb.append(prepareTable(meta));
		sqlSb.append(" SET");
		meta.fieldToColumnMapping().forEach((field, column) -> {
			if (!meta.idField().equals(field)) {
				sqlSb.append(' ').append(column).append('=').append(':').append(field).append(',');
			}
		});
		sqlSb.delete(sqlSb.length() - 1, sqlSb.length());
		sqlSb.append(" WHERE ").append(meta.idColumn()).append(" = :").append(meta.idField());

		return new SQLInfo().sql(sqlSb.toString()).parameter(new PrintableBeanPropertySqlParameterSource(model));
	}

	@Override
	public <T, ID> SQLInfo generateDelete(ModelRegister.MetaData<T> meta, ID id) {
		StringBuilder sqlSb = new StringBuilder("DELETE FROM ");
		sqlSb.append(prepareTable(meta));
		sqlSb.append(" WHERE ").append(meta.idColumn()).append(" = :").append(meta.idField());
		MapSqlParameterSource parameter = new PrintableMapSqlParameterSource();
		parameter.addValue(meta.idField(), id);
		return new SQLInfo().sql(sqlSb.toString()).parameter(parameter);
	}

	@Override
	public <T, ID> SQLInfo generateGetById(ModelRegister.MetaData<T> meta, ID id) {
		StringBuilder sqlSb = new StringBuilder("SELECT ");
		sqlSb.append(meta.columnsStr()).append(" FROM ");
		sqlSb.append(prepareTable(meta));
		sqlSb.append(" WHERE ").append(meta.idColumn()).append(" = :").append(meta.idField());
		MapSqlParameterSource parameter = new PrintableMapSqlParameterSource();
		parameter.addValue(meta.idField(), id);
		return new SQLInfo().sql(sqlSb.toString()).parameter(parameter);
	}

	protected <T> String prepareTable(ModelRegister.MetaData<T> meta) {
		StringBuilder tableSb = new StringBuilder();
		if (StringUtils.hasText(meta.catalog())) {
			tableSb.append(meta.catalog()).append('.');
		}
		if (StringUtils.hasText(meta.schema())) {
			tableSb.append(meta.schema()).append('.');
		}
		return tableSb.append(meta.table()).toString();
	}

	protected <T> SQLColumnsInfo prepareColumns(ModelRegister.MetaData<T> meta, T model) {
		SQLColumnsInfo info = new SQLColumnsInfo();
		StringBuilder columnsSB = new StringBuilder();
		StringBuilder placeholdersSB = new StringBuilder();
		BeanMap bean = BeanMap.create(model);

		IdInfo idInfo = generateID(meta, model);
		if (StringUtils.hasText(idInfo.idColumn())) {
			columnsSB.append(idInfo.idColumn()).append(',');
			placeholdersSB.append(':').append(idInfo.idField()).append(',');
			bean.put(idInfo.idField(), idInfo.idValue());
		}

		for (String field : meta.fieldToColumnMapping().keySet()) {
			if (!meta.idField().equals(field) && bean.get(field) != null) {
				String column = meta.fieldToColumnMapping().get(field);
				columnsSB.append(column).append(',');
				placeholdersSB.append(':').append(field).append(',');
			}
		}
		String columns = columnsSB.substring(0, columnsSB.length() - 1);
		String placeholders = placeholdersSB.substring(0, placeholdersSB.length() - 1);

		return info.columns(columns).placeholders(placeholders).parameter(new PrintableBeanPropertySqlParameterSource(model));
	}

	@SneakyThrows
	private <T> IdInfo generateID(MetaData<T> meta, T model) {
		return new IdInfo().idField(meta.idField()).idColumn(meta.idColumn()).idValue(meta.idGenerator().newInstance().generateId(model));
	}

	@Override
	public <T> String generateSelect(String columns, String table, String where, List<OrderBy> orderBy, Integer limit, Integer offset) {
		StringBuilder sqlSb = new StringBuilder("SELECT ");
		sqlSb.append(columns);
		sqlSb.append(" FROM ").append(table);
		sqlSb.append(" WHERE ").append(where);
		if (!CollectionUtils.isEmpty(orderBy)) {
			sqlSb.append(" ORDER BY ");
			orderBy.forEach(o -> {
				sqlSb.append(o.column()).append(o.desc() ? " DESC," : ",");
			});
			sqlSb.delete(sqlSb.length() - 1, sqlSb.length());
		}

		return addLimitClause(sqlSb.toString(), limit, offset);
	}

	@Override
	public String generateCount(String generatedSQL) {
		StringBuilder sqlSb = new StringBuilder("SELECT count(*) FROM (");
		sqlSb.append(generatedSQL);
		sqlSb.append(") _T");
		return sqlSb.toString();
	}

	@Override
	public <T> SQLInfo generateUpdateByConditions(MetaData<T> metaData, String sqlWhere, T model, Object[] parameters) {
		StringBuilder sqlSb = new StringBuilder("UPDATE ");
		sqlSb.append(prepareTable(metaData));
		sqlSb.append(" SET");
		metaData.fieldToColumnMapping().forEach((field, column) -> {
			if (!metaData.idField().equals(field)) {
				sqlSb.append(' ').append(column).append('=').append(':').append(field).append(',');
			}
		});
		sqlSb.delete(sqlSb.length() - 1, sqlSb.length());
		sqlSb.append(" WHERE ");

		ProcessSqlWhereReturn ps = processSqlWhere(sqlWhere, model, parameters);
		sqlSb.append(ps.sql);

		return new SQLInfo().sql(sqlSb.toString()).parameter(new PrintableMapSqlParameterSource(ps.mapParams));
	}

	@Override
	public <T> SQLInfo generateDeleteByConditions(MetaData<T> metaData, String sqlWhere, T model, Object[] parameters) {
		StringBuilder sqlSb = new StringBuilder("DELETE FROM ");
		sqlSb.append(prepareTable(metaData));
		sqlSb.append(" WHERE ");

		ProcessSqlWhereReturn ps = processSqlWhere(sqlWhere, model, parameters);
		sqlSb.append(ps.sql);

		return new SQLInfo().sql(sqlSb.toString()).parameter(new PrintableMapSqlParameterSource(ps.mapParams));
	}

	@SuppressWarnings("unchecked")
	private ProcessSqlWhereReturn processSqlWhere(String sqlWhere, Object model, Object[] parameters) {
		StringBuilder newWhereSb = new StringBuilder();
		int[] position = { 0 };
		Map<String, Object> mapParams = Maps.newLinkedHashMap(BeanMap.create(model));
		sqlWhere.chars().forEach(c -> {
			if (c == '?') {
				String key = "_sql_param_" + position[0];
				newWhereSb.append(':').append(key);
				mapParams.put(key, parameters[position[0]]);
				position[0]++;
			} else {
				newWhereSb.append((char) c);
			}
		});

		return new ProcessSqlWhereReturn(newWhereSb.toString(), mapParams);
	}

	private class ProcessSqlWhereReturn {
		String sql;
		Map<String, Object> mapParams;

		ProcessSqlWhereReturn(String sql, Map<String, Object> mapParams) {
			this.sql = sql;
			this.mapParams = mapParams;
		}
	}

	protected abstract String addLimitClause(String sql, Integer limit, Integer offset);

}
