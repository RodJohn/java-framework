package com.bmo.ibackend.test;

import com.bmo.ibackend.persistence.Column;
import com.bmo.ibackend.persistence.Id;
import com.bmo.ibackend.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Table
public class People {
	@Id
	@Column
	Integer id; // SQL Type: INT size:10
	@Column
	String firstName; // SQL Type: VARCHAR size:56
	@Column
	String lastName; // SQL Type: VARCHAR size:56
	@Column
	java.time.LocalDateTime createdAt; // SQL Type: DATETIME size:19
	@Column
	java.time.LocalDateTime updatedAt; // SQL Type: DATETIME size:19
}
