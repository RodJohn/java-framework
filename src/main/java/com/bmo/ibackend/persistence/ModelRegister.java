package com.bmo.ibackend.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.bmo.ibackend.AppConfig.ContextHolder;
import com.bmo.ibackend.persistence.id.IDGenerator;
import com.bmo.ibackend.persistence.sqlgen.NamingConvention;
import com.bmo.ibackend.util.AnnotationScanner;
import com.bmo.ibackend.util.JDBCRowMapperFactory;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ModelRegister {
	@Setter
	@Getter
	@Accessors(fluent = true, chain = true)
	private static NamingConvention namingConvention = ContextHolder.getContext().getBean(NamingConvention.class);
	@Getter
	@Accessors(fluent = true, chain = true)
	private static Map<String, MetaData<?>> metaDataCache = Maps.newConcurrentMap();

	@Data
	@Accessors(fluent = true, chain = true)
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class MetaData<T> {
		Class<T> modelClass;

		String table;

		String catalog;

		String schema;

		Map<String, String> fieldToColumnMapping = new LinkedHashMap<>();

		Map<String, String> columnToFieldMapping = new LinkedHashMap<>();

		Map<String, Class<?>> fieldTypes = new LinkedHashMap<>();

		String columnsStr;

		String idColumn;

		String idField;

		Class<? extends IDGenerator> idGenerator;
	}

	@Getter
	@Accessors(fluent = true, chain = true)
	public static class ModelRegisterEvent extends ApplicationEvent {
		private static final long serialVersionUID = 1929598449603342772L;

		public static final int TABLE = 0;
		public static final int VIEW = 1;

		String viewSql;
		MetaData<?> metaData;
		int type = TABLE;

		public ModelRegisterEvent(Object source, String viewSql, MetaData<?> metaData) {
			super(source);
			this.viewSql = viewSql;
			this.metaData = metaData;
			this.type = VIEW;
		}

		public ModelRegisterEvent(Object source, MetaData<?> metaData) {
			super(source);
			this.metaData = metaData;
			this.type = TABLE;
		}

	}

	public static void preload(String... packageBases) {
		AnnotationScanner.scan(Table.class, packageBases, beanDefine -> {
			try {
				Class<?> claz = ClassUtils.forName(beanDefine.getBeanClassName(), ModelRegister.class.getClassLoader());
				getMetaData(claz);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static <T> MetaData<T> register(Class<T> modelClass) {
		MetaData<T> metaData = new MetaData<>();
		metaData.modelClass = modelClass;
		processClassAnnotation(modelClass, metaData);
		processFieldAnnotations(modelClass, metaData);
		metaData.fieldToColumnMapping = Collections.unmodifiableMap(metaData.fieldToColumnMapping);
		metaData.columnToFieldMapping = Collections.unmodifiableMap(metaData.columnToFieldMapping);
		metaData.fieldTypes = Collections.unmodifiableMap(metaData.fieldTypes);
		getJDBCRowMapper(modelClass);
		return metaData;
	}

	@SuppressWarnings("unchecked")
	public static <T> MetaData<T> getMetaData(Class<T> modelClass) {
		String cacheKey = modelClass.getName();
		MetaData<T> meta = (MetaData<T>) metaDataCache.get(cacheKey);
		if (meta == null || meta.modelClass() != modelClass) {
			meta = register(modelClass);
			metaDataCache.put(cacheKey, meta);
			if (log.isDebugEnabled()) {
				log.debug("Loaded Persistence Model: {} ({})", modelClass, meta);
			}
		}
		return meta;
	}

	public static <T> JDBCRowMapper<T> getJDBCRowMapper(Class<T> modelClass) {
		return JDBCRowMapperFactory.getJDBCRowMapper(modelClass);
	}

	private static <T> void processClassAnnotation(Class<T> modelClass, MetaData<T> meta) {
		Table table = AnnotationUtils.findAnnotation(modelClass, Table.class);
		if (table != null) {
			_processTable(table, meta);
			return;
		}
		Record record = AnnotationUtils.findAnnotation(modelClass, Record.class);
		if (record != null) {
			return;
		}
		Assert.isTrue(false, "Persistence model class must have a @Table/@Record/@SQLView annotation.");
	}

	private static <T> void _processTable(Table table, MetaData<T> meta) {
		if (StringUtils.isEmpty(table.name())) {
			meta.table = namingConvention.convertJavaToSQL(meta.modelClass().getSimpleName());
		} else {
			meta.table = table.name();
		}
		meta.catalog = table.catalog();
		meta.schema = table.schema();
	}

	private static <T> void processFieldAnnotations(Class<T> modelClass, MetaData<T> meta) {
		Arrays.stream(modelClass.getDeclaredFields()).forEach(field -> {
			Column[] columns = field.getAnnotationsByType(Column.class);
			Id[] ids = field.getAnnotationsByType(Id.class);
			if (columns.length > 0) {
				Column column = columns[columns.length - 1];
				if (StringUtils.isEmpty(column.name())) {
					meta.fieldToColumnMapping.put(field.getName(), namingConvention.convertJavaToSQL(field.getName()));
				} else {
					meta.fieldToColumnMapping.put(field.getName(), column.name());
				}
				meta.columnToFieldMapping.put(meta.fieldToColumnMapping.get(field.getName()), field.getName());
				meta.fieldTypes.put(field.getName(), field.getType());
			}
			if (ids.length > 0) {
				meta.idField = field.getName();
				meta.idColumn = namingConvention.convertJavaToSQL(field.getName());
				meta.idGenerator = ids[ids.length - 1].value();
			}
		});
		meta.columnsStr = meta.fieldToColumnMapping.values().stream().collect(Collectors.joining(", "));
		ContextHolder.getContext().publishEvent(new ModelRegisterEvent(meta, meta));
	}
}
