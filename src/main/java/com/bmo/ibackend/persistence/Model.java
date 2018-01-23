package com.bmo.ibackend.persistence;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import com.bmo.ibackend.AppConfig.ContextHolder;
import com.bmo.ibackend.persistence.ModelRegister.MetaData;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator.SQLInfo;
import com.bmo.ibackend.util.SQLLogger;
import com.bmo.ibackend.util.ScopeMaker;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.sf.cglib.beans.BeanMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Model<T, ID> {
	ModelRegister.MetaData<T> metaData;
	NamedParameterJdbcTemplate jdbcTemplate = ContextHolder.getContext().getBean(NamedParameterJdbcTemplate.class);
	SQLGenerator sqlgen = ContextHolder.getContext().getBean(SQLGenerator.class);

	public static void preload(String... packageBases) {
		ModelRegister.preload(packageBases);
	}

	public Model(Class<T> modelClass) {
		super();
		this.metaData = ModelRegister.getMetaData(modelClass);
		Assert.notNull(this.metaData.table(), "Persistence model class must have a @Table annotation.");
	}

	public Class<T> modelClass() {
		return metaData.modelClass();
	}

	public MetaData<T> metaData() {
		return metaData;
	}

	public T getById(@NonNull ID id) {
		SQLInfo sqlInfo = sqlgen.generateGetById(metaData(), id);
		SQLLogger.log(sqlInfo.sql(), sqlInfo.parameter());
		List<T> resultList = jdbcTemplate.query(sqlInfo.sql(), sqlInfo.parameter(), ModelRegister.getJDBCRowMapper(modelClass()));
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	public void saveOrUpdate(T model) {
		BeanMap bean = BeanMap.create(model);
		if (bean.get(metaData.idField()) == null) {
			SQLInfo sqlInfo = sqlgen.generateInsert(metaData(), model);
			SQLLogger.log(sqlInfo.sql(), sqlInfo.parameter());
			jdbcTemplate.update(sqlInfo.sql(), sqlInfo.parameter());
		} else {
			SQLInfo sqlInfo = sqlgen.generateUpdate(metaData(), model);
			SQLLogger.log(sqlInfo.sql(), sqlInfo.parameter());
			jdbcTemplate.update(sqlInfo.sql(), sqlInfo.parameter());
		}
	}

	@SuppressWarnings("unchecked")
	public void delete(T model) {
		BeanMap bean = BeanMap.create(model);
		ID id = (ID) bean.get(metaData.idField());
		deleteById(id);
	}

	public void deleteById(@NonNull ID id) {
		SQLInfo sqlInfo = sqlgen.generateDelete(metaData(), id);
		SQLLogger.log(sqlInfo.sql(), sqlInfo.parameter());
		jdbcTemplate.update(sqlInfo.sql(), sqlInfo.parameter());
	}

	public void updateWhere(T model, String sqlWhere, Object... parameters) {
		Assert.hasText(sqlWhere, "Must provide SQL where clause.");
		SQLInfo sqlInfo = sqlgen.generateUpdateByConditions(metaData(), sqlWhere, model, parameters);
		String sql = sqlgen.evalELSql(metaData, sqlInfo.sql(), ScopeMaker.make(metaData, parameters));
		SQLLogger.log(sql, sqlInfo.parameter());
		jdbcTemplate.update(sql, sqlInfo.parameter());
	}

	public void deleteWhere(T model, String sqlWhere, Object... parameters) {
		Assert.hasText(sqlWhere, "Must provide SQL where clause.");
		SQLInfo sqlInfo = sqlgen.generateDeleteByConditions(metaData(), sqlWhere, model, parameters);
		String sql = sqlgen.evalELSql(metaData, sqlInfo.sql(), ScopeMaker.make(metaData, parameters));
		SQLLogger.log(sql, sqlInfo.parameter());
		jdbcTemplate.update(sql, sqlInfo.parameter());
	}

	public ActiveRecordQuery<T> where(String sqlWhere, Object... parameters) {
		Assert.hasText(sqlWhere, "Must provide SQL where clause.");
		return new ActiveRecordQuery<>(sqlgen, metaData, sqlWhere, parameters);
	}

	public ActiveRecordQuery<T> match(T sample) {
		return new ActiveRecordQuery<>(sqlgen, metaData, sample);
	}

}
