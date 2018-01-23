package com.bmo.ibackend.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bmo.ibackend.persistence.id.IDGenerator;
import com.bmo.ibackend.persistence.id.UUIDGenerator;

/**
 * Specifies the primary key property or field of an entity.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Id {
	Class<? extends IDGenerator> value() default UUIDGenerator.class;
}