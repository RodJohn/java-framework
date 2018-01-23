package com.bmo.ibackend.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bmo.ibackend.demo.model.People;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemoService {

	public List<People> getPersons(String name) {
		People people = new People();
//		people.
		log.debug("Demo", "test {}", new Date());
		return null;
	}
}
