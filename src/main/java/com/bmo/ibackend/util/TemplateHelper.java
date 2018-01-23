package com.bmo.ibackend.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TemplateHelper {
	private static Map<Integer, Mustache> templateCache = new ConcurrentHashMap<>();
	private static MustacheFactory mf = new DefaultMustacheFactory();

	@SneakyThrows
	public static String eval(String template, Map<String, Object> scopes) {
		Writer writer = new StringWriter();
		Mustache mustache = getMustache(template);
		mustache.execute(writer, scopes);
		writer.flush();
		return writer.toString();
	}

	private static Mustache getMustache(String template) {
		Integer hash = Objects.hashCode(template);
		Mustache mustache = templateCache.get(hash);
		if (mustache == null) {
			mustache = mf.compile(new StringReader(template), "T" + hash);
			templateCache.put(hash, mustache);
		}
		return mustache;
	}

}
