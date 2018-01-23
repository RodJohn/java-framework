package com.bmo.ibackend.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the primary table for the annotated entity.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMapper {
	/**
	 * (Optional) The name of the table.
	 * <p>
	 * Defaults to the class name.
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends JDBCRowMapper> value() default DefaultBeanPropertyRowMapper.class;

}
