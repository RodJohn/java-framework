package com.bmo.ibackend.persistence;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.bmo.ibackend.Application;

import lombok.Data;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(Application.class)
@Transactional
public class SQLQueryTest {

	@Data
	@Record
	private static class NameVo {
		@Column
		String name;
	};

	@Test
	public void sqlQuery_select() {
		Model<TestTable, String> model = new Model<>(TestTable.class);
		Set<TestTable> poes = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			TestTable po = new TestTable();
			po.setFirstName("tester");
			po.setLastName("MyLastName_" + i);
			po.setCreatedAt(LocalDateTime.now());
			model.saveOrUpdate(po);
			poes.add(po);
		}

		NameVo vo = SQLQuery.sql(NameVo.class, "select CONCAT(first_name,last_name) name from test_table where first_name =? order by name", "tester").first();
		Assert.assertTrue(vo.getName().startsWith("testerMyLastName_"));

		SQLQuery.sql(TestTable.class, "select * from test_table where last_name like ?", "MyLastName_%").all().forEach(tt -> {
			Assert.assertTrue(tt.getLastName().startsWith("MyLastName_"));
		});

	}

	@Test
	public void sqlQuery_update() {
		Model<TestTable, String> model = new Model<>(TestTable.class);
		Set<TestTable> poes = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			TestTable po = new TestTable();
			po.setFirstName("tester");
			po.setLastName("MyLastName_" + i);
			po.setCreatedAt(LocalDateTime.now());
			model.saveOrUpdate(po);
			poes.add(po);
		}

		int res = SQLQuery.sql("update test_table set updated_at=null where first_name =?", "tester").update();
		Assert.assertEquals(res, 10);
	}

	@Test
	public void sqlQuery_exec() {
		Model<TestTable, String> model = new Model<>(TestTable.class);
		Set<TestTable> poes = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			TestTable po = new TestTable();
			po.setFirstName("tester");
			po.setLastName("MyLastName_" + i);
			po.setCreatedAt(LocalDateTime.now());
			model.saveOrUpdate(po);
			poes.add(po);
		}

		SQLQuery.sql("update test_table set updated_at=null").exec();
	}
}
