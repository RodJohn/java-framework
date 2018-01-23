package com.bmo.ibackend.persistence.javagen;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bmo.ibackend.persistence.javagen.TableClassGenerator.JavaTemplateVO;
import com.bmo.ibackend.persistence.sqlgen.NamingConvention;
import com.google.common.collect.Lists;

import lombok.Cleanup;
import lombok.SneakyThrows;

@SpringBootApplication(scanBasePackages = { "com.dbs" })
public class JavaFileGenerator {

	@SneakyThrows
	public static void main(String[] args) {
		ConfigurableApplicationContext spring = SpringApplication.run(JavaFileGenerator.class, args);
		try {
			DataSource datasource = spring.getBean(DataSource.class);
			NamingConvention nc = spring.getBean(NamingConvention.class);
			@Cleanup
			Connection conn = datasource.getConnection();
			DatabaseMetaData md = conn.getMetaData();
			@Cleanup
			InputStream in = spring.getResource("classpath:codegen/tables.properties").getInputStream();
			Properties p = new Properties();
			p.load(in);
			String tablePattern = p.getProperty("code.gen.table.pattern", "%");
			ResultSet rs = md.getTables(null, null, tablePattern, new String[] { "TABLE" });
			// showRS(rs);
			String packageName = p.getProperty("code.gen.java.package");
			String codePath = p.getProperty("code.gen.java.path");

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				ResultSet resultSet = md.getColumns(null, null, tableName, null);
				String pk = getPK(tableName, md);
				String className = StringUtils.capitalize(nc.convertSQLtoJava(tableName));
				List<JavaTemplateVO> fields = Lists.newArrayList();
				while (resultSet.next()) {
					String name = resultSet.getString("COLUMN_NAME");
					String type = resultSet.getString("TYPE_NAME");
					int size = resultSet.getInt("COLUMN_SIZE");

					JavaTemplateVO vo = new JavaTemplateVO();
					vo.setFieldName(nc.convertSQLtoJava(name));
					vo.setSqlType(type);
					vo.setSize(size);
					if (name.equals(pk)) {
						vo.setId(true);
					}
					vo.setFieldType(SQLTypeToJavaTypeMapper.map(type, tableName, name));
					fields.add(vo);
				}

				String javaFileString = TableClassGenerator.generate(spring, packageName, className, fields);
				File file = new File(codePath + "/" + packageName.replaceAll("\\.", "/") + "/" + className + ".java");
				System.out.println("Generating Java File: " + file);
				FileUtils.write(file, javaFileString, Charset.forName("UTF-8"));
			}
		} finally {
			spring.close();
		}
	}

	private static String getPK(String tableName, DatabaseMetaData md) throws Exception {
		ResultSet rs = md.getPrimaryKeys(null, null, tableName);
		// showRS(rs);
		String pkey = null;
		while (rs.next()) {
			pkey = rs.getString("COLUMN_NAME");
		}

		return pkey;
	}

	@SneakyThrows
	private static void showRS(ResultSet rs) {
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			System.out.print(rs.getMetaData().getColumnName(i + 1) + " | ");
		}
		System.out.println();
		while (rs.next()) {
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				System.out.print(rs.getString(i + 1));
				System.out.print(" | ");
			}
			System.out.println();
		}
	}

}
