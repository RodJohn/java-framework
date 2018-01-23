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
public class TestTable {
	@Id
	@Column
	String id; // SQL Type: VARCHAR size:36
	@Column
	String firstName; // SQL Type: VARCHAR size:56
	@Column
	String lastName; // SQL Type: VARCHAR size:56
	@Column
	java.time.LocalDateTime createdAt; // SQL Type: TIMESTAMP size:23
	@Column
	java.time.LocalDateTime updatedAt; // SQL Type: TIMESTAMP size:23
	@Column
	Boolean deleted; // SQL Type: BIT size:1
	@Column
	Integer testTablecol; // SQL Type: INT size:10
}
