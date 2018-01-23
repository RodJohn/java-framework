package com.bmo.ibackend.persistence.javagen;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ConfigurableApplicationContext;

import com.bmo.ibackend.util.TemplateHelper;
import com.google.common.collect.Maps;

import lombok.Cleanup;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TableClassGenerator {
	@Data
	public static class JavaTemplateVO {
		String fieldName, sqlType, fieldType;
		boolean id = false;
		int size;
	}

	public static String generate(ConfigurableApplicationContext spring, String packageName, String className, List<JavaTemplateVO> fields) throws Exception {
		@Cleanup
		InputStreamReader in = new InputStreamReader(spring.getResource("classpath:com/dbs/ibackend/persistence/javagen/Table.template").getInputStream());
		StringWriter out = new StringWriter();
		IOUtils.copy(in, out);
		String template = out.toString();
		Map<String, Object> scopes = Maps.newHashMap();
		scopes.put("packageName", packageName);
		scopes.put("className", className);
		scopes.put("fields", fields);

		return TemplateHelper.eval(template, scopes);
	}
}
