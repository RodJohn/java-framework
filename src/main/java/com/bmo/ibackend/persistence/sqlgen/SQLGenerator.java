package com.bmo.ibackend.persistence.sqlgen;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.bmo.ibackend.persistence.ActiveRecordQuery.OrderBy;
import com.bmo.ibackend.persistence.ModelRegister;
import com.bmo.ibackend.persistence.ModelRegister.MetaData;
import com.bmo.ibackend.util.TemplateHelper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

public interface SQLGenerator {

	@Data
	@Accessors(fluent = true, chain = true)
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class SQLColumnsInfo {
		String columns;
		String placeholders;
		SqlParameterSource parameter;
	}

	@Data
	@Accessors(fluent = true, chain = true)
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class IdInfo {
		String idColumn;
		String idField;
		Object idValue;
	}

	@Data
	@Accessors(fluent = true, chain = true)
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class SQLInfo {
		String sql;
		SqlParameterSource parameter;
	}

	default <T> String evalELSql(MetaData<T> meta, String elSql, Map<String, Object> scopes) {
		return TemplateHelper.eval(elSql, scopes);
	}

	<T> SQLInfo generateInsert(ModelRegister.MetaData<T> metaData, T model);

	<T> SQLInfo generateUpdate(ModelRegister.MetaData<T> metaData, T model);

	<T, ID> SQLInfo generateDelete(ModelRegister.MetaData<T> metaData, ID id);

	<T, ID> SQLInfo generateGetById(ModelRegister.MetaData<T> metaData, ID id);

	<T> String generateSelect(String columns, String table, String where, List<OrderBy> orderBy, Integer limit, Integer offset);

	String generateCount(String generatedSQL);

	<T> SQLInfo generateUpdateByConditions(MetaData<T> metaData, String sqlWhere, T model, Object[] parameters);

	<T> SQLInfo generateDeleteByConditions(MetaData<T> metaData, String sqlWhere, T model, Object[] parameters);

}
