package com.bmo.ibackend.persistence;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.bmo.ibackend.Application;
import com.bmo.ibackend.persistence.sqlgen.DefaultNamingConvention;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator;
import com.bmo.ibackend.persistence.sqlgen.SQLGenerator.SQLInfo;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(Application.class)
@Slf4j
public class SQLGeneratorTests {
	@Autowired
	SQLGenerator sqlgen;

	@Test
	public void generateInsert() {
		ModelRegister.MetaData<TestTable> metaData = ModelRegister.getMetaData(TestTable.class);
		final String expectedSQL = "INSERT INTO test.TEST_TABLE(ID,FIRST_NAME,LAST_NAME,CREATED_AT,UPDATED_AT) VALUES (:id,:firstName,:lastName,:createdAt,:updatedAt)";
		TestTable model = new TestTable();
		model.setFirstName("firstName");
		model.setLastName("lastName");
		LocalDateTime date = LocalDateTime.now();
		model.setCreatedAt(date);
		model.setUpdatedAt(date);

		SQLInfo sqlInfo = sqlgen.generateInsert(metaData, model);
		log.debug("Generated SQL: {}", sqlInfo.sql());
		log.debug("Parameter for the SQL: {}", sqlInfo.parameter());
		Assert.assertEquals(expectedSQL, sqlInfo.sql());
	}

	@Test
	public void generateInsertWithNullValue() {
		ModelRegister.MetaData<TestTable> metaData = ModelRegister.getMetaData(TestTable.class);
		final String expectedSQL = "INSERT INTO test.TEST_TABLE(ID,FIRST_NAME,LAST_NAME) VALUES (:id,:firstName,:lastName)";
		TestTable model = new TestTable();
		model.setFirstName("firstName");
		model.setLastName("lastName");

		SQLInfo sqlInfo = sqlgen.generateInsert(metaData, model);
		log.debug("Generated SQL: {}", sqlInfo.sql());
		log.debug("Parameter for the SQL: {}", sqlInfo.parameter());
		Assert.assertEquals(expectedSQL, sqlInfo.sql());
	}

	@Test
	public void convertClassToSQLView() {
		DefaultNamingConvention dnc = new DefaultNamingConvention();
		String view = dnc.convertClassToSQLView(TestTable.class);
		System.out.println(view);
	}
}
