package com.bmo.ibackend.persistence.sqlgen;

import org.springframework.context.ApplicationListener;

import com.bmo.ibackend.persistence.Column;
import com.bmo.ibackend.persistence.ModelRegister.ModelRegisterEvent;
import com.bmo.ibackend.persistence.Record;
import com.bmo.ibackend.persistence.SQLQuery;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySQLGenerator extends BasicSQLSQLGenerator implements ApplicationListener<ModelRegisterEvent> {
	static {
		log.debug("Set DB support to MySQL/MariaDB.");
	}

	@Override
	protected String addLimitClause(String sql, Integer limit, Integer offset) {
		if (limit != null) {
			StringBuilder sqlSb = new StringBuilder(sql);
			sqlSb.append(" LIMIT ").append(limit);
			if (offset != null) {
				sqlSb.append(" OFFSET ").append(offset);
			}
			return sqlSb.toString();
		}
		return sql;
	}

	// private String generateGetAllViewsSQL() {
	// return "SELECT TABLE_NAME FROM information_schema.`TABLES` WHERE TABLE_TYPE
	// LIKE 'VIEW'";
	// }

	private String generateGetCreateViewsSQL(String viewName, String sql) {
		StringBuilder sqlSb = new StringBuilder("CREATE OR REPLACE VIEW ");
		sqlSb.append(viewName).append(" AS ").append(sql);
		return sqlSb.toString();
	}

	@Record
	@Data
	private class AllViews {
		@Column
		String tableName;
	}

	@Override
	public void onApplicationEvent(ModelRegisterEvent event) {
		if (event.type() == ModelRegisterEvent.VIEW) {
			log.debug("Checking SQL View: {}", event.metaData().table());
			SQLQuery.sql(generateGetCreateViewsSQL(event.metaData().table(), event.viewSql())).exec();

		}
	}
}
