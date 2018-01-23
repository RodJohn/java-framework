package com.bmo.ibackend.persistence.sqlgen;

public interface NamingConvention {
	String convertJavaToSQL(String javaNaming);

	String convertSQLtoJava(String sqlNaming);

	String convertClassToSQLView(Class<?> claz);
}
