package com.bmo.ibackend;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bmo.ibackend.persistence.Model;
import com.bmo.ibackend.persistence.sqlgen.DefaultNamingConvention;
import com.bmo.ibackend.persistence.sqlgen.NamingConvention;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@PropertySource("classpath:/datasource.properties")
public class AppConfig {
	@Value("${datasource.jdbcUrl}")
	private String jdbcUrl;

	@Value("${datasource.driverClassName}")
	private String driverClassName;

	@Value("${datasource.user}")
	private String userName;

	@Value("${datasource.password}")
	private String password;

	@Value("${datasource.poolSize}")
	private int poolSize;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(userName);
		config.setPassword(password);
		config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("useServerPrepStmts", "true");

		final HikariDataSource ds = new HikariDataSource(config);

		return ds;
	}

	@Bean
	public NamingConvention namingConvention() {
		return new DefaultNamingConvention();
	}

	@Component
	public static class ContextHolder {
		private static ApplicationContext context;

		@Autowired
		public ContextHolder(ApplicationContext ac) {
			context = ac;
		}

		public static ApplicationContext getContext() {
			return context;
		}
	}

	@Component
	@Slf4j
	public static class ApplicationListenerBean implements ApplicationListener<ContextRefreshedEvent> {

		@Value("${app.base.package}")
		private String basePackage;

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			log.debug("Scanning... {}", basePackage);
			Model.preload(basePackage);
		}
	}

}
