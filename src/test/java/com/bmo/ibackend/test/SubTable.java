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
public class SubTable {
	@Id
	@Column
	String id; // SQL Type: VARCHAR size:36
	@Column
	String subName; // SQL Type: TEXT size:65535
	@Column
	String testTableId; // SQL Type: VARCHAR size:45
}
