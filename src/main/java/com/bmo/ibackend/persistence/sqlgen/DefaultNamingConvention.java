package com.bmo.ibackend.persistence.sqlgen;

import com.google.common.base.CaseFormat;

public class DefaultNamingConvention implements NamingConvention {

	@Override
	public String convertJavaToSQL(String javaNaming) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaNaming);
	}

	@Override
	public String convertSQLtoJava(String sqlNaming) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, sqlNaming.toUpperCase());
	}

	@Override
	public String convertClassToSQLView(Class<?> claz) {
		String name = new StringBuilder(getPrefix(claz)).append('_').append(claz.getSimpleName()).toString();
		return name.toLowerCase();
	}

	private String getPrefix(Class<?> claz) {
		Package pkg = claz.getPackage();
		if (pkg != null) {
			int p = pkg.getName().lastIndexOf('.');
			if (p > -1) {
				String prefix = pkg.getName().substring(p + 1);
				return prefix;
			} else {
				return pkg.getName();
			}
		} else {
			return "default";
		}
	}
}
