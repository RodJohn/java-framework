package com.bmo.ibackend.persistence;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Table(schema = "test")
public class TestTable {
	@Column
	@Id
	String id;
	@Column
	String firstName;
	@Column
	String lastName;
	@Column
	LocalDateTime createdAt;
	@Column
	LocalDateTime updatedAt;
	@Column
	Integer deleted = 0;
}
