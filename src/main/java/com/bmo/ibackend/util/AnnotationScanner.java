package com.bmo.ibackend.util;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AnnotationScanner {
	public static void scan(Class<? extends Annotation> annoClass, String[] packageBases, Consumer<BeanDefinition> action) {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(annoClass));
		for (String packageBase : packageBases) {
			scanner.findCandidateComponents(packageBase).forEach(bean -> {
				action.accept(bean);
			});
		}
	}
}
