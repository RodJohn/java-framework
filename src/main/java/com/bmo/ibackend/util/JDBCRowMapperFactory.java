package com.bmo.ibackend.util;

import java.util.Map;

import org.springframework.core.annotation.AnnotationUtils;

import com.bmo.ibackend.persistence.DefaultBeanPropertyRowMapper;
import com.bmo.ibackend.persistence.JDBCRowMapper;
import com.bmo.ibackend.persistence.ResultMapper;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JDBCRowMapperFactory {
	@AllArgsConstructor
	private static class RowMapperHolder {
		Class<?> modelClass;
		JDBCRowMapper<?> mapper;
	}

	private static Map<String, RowMapperHolder> rowMappersCache = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public static <T> JDBCRowMapper<T> getJDBCRowMapper(Class<T> modelClass) {
		String cacheKey = modelClass.getName();
		RowMapperHolder holder = rowMappersCache.get(cacheKey);
		if (holder == null || holder.modelClass != modelClass) {
			ResultMapper mapper = AnnotationUtils.findAnnotation(modelClass, ResultMapper.class);

			Class<?> mapperClass = DefaultBeanPropertyRowMapper.class;
			if (mapper != null) {
				mapperClass = mapper.value();
			}

			JDBCRowMapper<T> rmapper = (JDBCRowMapper<T>) mapperClass.newInstance();
			rmapper.setMappedClass(modelClass);

			holder = new RowMapperHolder(modelClass, rmapper);

			rowMappersCache.put(cacheKey, holder);
			log.debug("Loaded RowMapper for [{}]", modelClass);
		}
		return (JDBCRowMapper<T>) holder.mapper;
	}
}
