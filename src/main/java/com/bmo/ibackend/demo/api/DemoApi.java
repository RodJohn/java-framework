package com.bmo.ibackend.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmo.ibackend.demo.model.People;
import com.bmo.ibackend.demo.service.DemoService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/demo/api")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemoApi {

	@Autowired
	DemoService demoService;

	@GetMapping("/persons/{name}")
	List<People> people(@PathVariable String name) {
		log.debug("name is: {}", name);
		List<People> persons = demoService.getPersons(name);
		return persons;
	}
}
