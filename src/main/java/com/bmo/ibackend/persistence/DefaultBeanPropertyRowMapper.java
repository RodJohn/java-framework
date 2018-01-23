package com.bmo.ibackend.persistence;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.bmo.ibackend.persistence.ModelRegister.MetaData;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultBeanPropertyRowMapper<T> implements JDBCRowMapper<T> {
	/** The class we are mapping to */
	private Class<T> mappedClass;

	/** ConversionService for binding JDBC values to bean properties */
	@Setter
	@Getter
	@Accessors(fluent = true)
	private ConversionService conversionService = DefaultConversionService.getSharedInstance();

	/**
	 * Extract the values for all columns in the current row.
	 * <p>
	 * Utilizes public setters and result set metadata.
	 * 
	 * @see java.sql.ResultSetMetaData
	 */
	@Override
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Assert.state(this.mappedClass != null, "Mapped class was not specified");
		T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
		initBeanWrapper(bw);

		MetaData<T> meta = ModelRegister.getMetaData(mappedClass);

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index);
			String field = meta.columnToFieldMapping().get(column.replaceAll(" ", ""));

			if (StringUtils.hasText(field)) {
				try {
					Object value = getColumnValue(rs, index, meta.fieldTypes().get(field));
					if (rowNumber == 0 && log.isDebugEnabled()) {
						log.debug("Mapping column '" + column + "' to property '" + field + "' of type '" + ClassUtils.getQualifiedName(meta.fieldTypes().get(field)) + "'");
					}
					try {
						bw.setPropertyValue(field, value);
					} catch (TypeMismatchException ex) {
						if (value == null) {
							if (log.isDebugEnabled()) {
								log.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '" + column + "' with null value when setting property '" + field + "' of type '" + ClassUtils.getQualifiedName(meta.fieldTypes().get(field)) + "' on object: " + mappedObject, ex);
							}
						} else {
							throw ex;
						}
					}
				} catch (NotWritablePropertyException ex) {
					throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + field + "'", ex);
				}
			} else {
				// No field found
				if (rowNumber == 0 && log.isDebugEnabled()) {
					log.debug("No field found for column '" + column + "' mapped to field '" + field + "'");
				}
			}
		}

		return mappedObject;
	}

	@Override
	public void setMappedClass(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
	}

	/**
	 * Initialize the given BeanWrapper to be used for row mapping. To be called for
	 * each row.
	 * <p>
	 * The default implementation applies the configured {@link ConversionService},
	 * if any. Can be overridden in subclasses.
	 * 
	 * @param bw
	 *            the BeanWrapper to initialize
	 * @see BeanWrapper#setConversionService
	 */
	protected void initBeanWrapper(BeanWrapper bw) {
		ConversionService cs = conversionService();
		if (cs != null) {
			bw.setConversionService(cs);
		}
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation calls
	 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
	 * Subclasses may override this to check specific value types upfront, or to
	 * post-process values return from {@code getResultSetValue}.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @param dataType
	 *            the bean property data type that each result object is expected to
	 *            match (or {@code null} if none specified)
	 * @return the Object value
	 * @throws SQLException
	 *             in case of extraction failure
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet,
	 *      int, Class)
	 */
	protected Object getColumnValue(ResultSet rs, int index, Class<?> dataType) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index, dataType);
	}
}
