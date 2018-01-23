package com.bmo.ibackend.demo.model;

import java.time.LocalDateTime;

import com.bmo.ibackend.persistence.Column;
import com.bmo.ibackend.persistence.Id;
import com.bmo.ibackend.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table
public class People {
	@Column
	@Id
	Integer id;
	@Column
	String firstName;
	@Column
	String lastName;
	@Column
	LocalDateTime createdAt;
	@Column
	LocalDateTime updatedAt;
}
