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

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(Application.class)
@Transactional
public class ModelTests {

	@Test
	public void save() {
		TestTable po = new TestTable();
		po.setFirstName("MyFirstName");
		po.setLastName("MyLastName");
		LocalDateTime date = LocalDateTime.now();
		po.setCreatedAt(date);
		po.setUpdatedAt(date);
		Model<TestTable, String> model = new Model<>(TestTable.class);
		model.saveOrUpdate(po);
		Assert.assertEquals(po, model.getById(po.getId()));
	}

	@Test
	public void update() {
		TestTable po = new TestTable();
		po.setFirstName("MyFirstName");
		po.setLastName("MyLastName");
		LocalDateTime date = LocalDateTime.now();
		po.setCreatedAt(date);
		po.setUpdatedAt(date);
		Model<TestTable, String> model = new Model<>(TestTable.class);
		model.saveOrUpdate(po);

		TestTable po1 = model.getById(po.getId());
		po1.setFirstName("AmendedFirstName");
		po1.setLastName("AmendedLastName");
		po1.setUpdatedAt(LocalDateTime.now());
		model.saveOrUpdate(po1);
		Assert.assertEquals(po1, model.getById(po1.getId()));
	}

	@Test
	public void saveOrUpdate() {
		// save first
		TestTable po = new TestTable();
		po.setFirstName("MyFirstName");
		po.setLastName("MyLastName");
		LocalDateTime date = LocalDateTime.now();
		po.setCreatedAt(date);
		po.setUpdatedAt(date);
		Model<TestTable, String> model = new Model<>(TestTable.class);
		model.saveOrUpdate(po);
		Assert.assertEquals(po, model.getById(po.getId()));

		// update then
		po.setFirstName("AmendedFirstName");
		po.setLastName("AmendedLastName");
		po.setUpdatedAt(LocalDateTime.now());
		model.saveOrUpdate(po);
		Assert.assertEquals(po, model.getById(po.getId()));
	}

	@Test
	public void delete() {
		TestTable po = new TestTable();
		po.setFirstName("MyFirstName");
		po.setLastName("MyLastName");
		LocalDateTime date = LocalDateTime.now();
		po.setCreatedAt(date);
		po.setUpdatedAt(date);
		Model<TestTable, String> model = new Model<>(TestTable.class);
		model.saveOrUpdate(po);
		model.delete(po);
		TestTable po1 = model.getById(po.getId());
		Assert.assertNull(po1);
	}

	@Test
	public void all() {
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
		model.where("{{firstName}} = ? and 1=?", "tester", 1).all().forEach(po -> {
			Assert.assertTrue(poes.contains(po));
		});
	}

	@Test
	public void first() {
		Model<TestTable, String> model = new Model<>(TestTable.class);
		TestTable last = null;
		for (int i = 0; i < 10; i++) {
			TestTable po = new TestTable();
			po.setFirstName("tester");
			po.setLastName("MyLastName_" + i);
			po.setCreatedAt(LocalDateTime.now());
			model.saveOrUpdate(po);
			last = po;
		}
		TestTable po = model.where("{{lastName}} = ? and 1=?", "MyLastName_9", 1).first();
		Assert.assertEquals(po, last);
	}

	@Test
	public void orderBy() {
		Model<TestTable, String> model = new Model<>(TestTable.class);
		TestTable last = null;
		for (int i = 0; i < 10; i++) {
			TestTable po = new TestTable();
			po.setFirstName("tester");
			po.setLastName("MyLastName_" + i);
			po.setCreatedAt(LocalDateTime.now());
			model.saveOrUpdate(po);
			last = po;
		}
		TestTable po = model.where("{{firstName}} = ? and 1=?", "tester", 1).orderByASC("{{firstName}}").orderByDESC("{{lastName}}").first();
		Assert.assertEquals(po, last);
	}

	@Test
	public void limit() {
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
		model.where("{{firstName}} = ? and 1=?", "tester", 1).limit(3).offset(2).all().forEach(po -> {
			System.out.println(po);
			Assert.assertTrue(poes.contains(po));
		});
	}

	@Test
	public void count() {
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
		int count = model.where("{{firstName}} = ? and 1=?", "tester", 1).count();
		Assert.assertEquals(count, 10);
	}

	@Test
	public void match() {
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
		TestTable sample = new TestTable();
		sample.setFirstName("tester");
		int count = model.match(sample).count();
		Assert.assertEquals(count, 10);
	}

	@Test
	public void updateWhere() {
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
		TestTable sample = new TestTable();
		sample.setFirstName("new_tester");
		sample.setUpdatedAt(LocalDateTime.now());

		model.updateWhere(sample, "{{firstName}} = ? and 1 = ?", "tester", 1);

		int count = model.match(sample).count();
		Assert.assertEquals(count, 10);
	}

	@Test
	public void deleteWhere() {
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
		TestTable sample = new TestTable();
		sample.setFirstName("new_tester");
		sample.setUpdatedAt(LocalDateTime.now());

		model.deleteWhere(sample, "{{firstName}} = ? and 1 = ?", "tester", 1);

		int count = model.match(sample).count();
		Assert.assertEquals(count, 0);
	}

}
