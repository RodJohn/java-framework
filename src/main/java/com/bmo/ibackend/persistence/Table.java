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
public @interface Table {
	/**
	 * (Optional) The name of the table.
	 * <p>
	 * Defaults to the class name.
	 */
	String name() default "";

	/**
	 * (Optional) The catalog of the table.
	 * <p>
	 * Defaults to the default catalog.
	 */
	String catalog() default "";

	/**
	 * (Optional) The schema of the table.
	 * <p>
	 * Defaults to the default schema for user.
	 */
	String schema() default "";
}
