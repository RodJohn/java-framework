package com.bmo.ibackend.util;

import com.google.common.collect.Lists;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SQLLogger {
	public static void log(String sql, Object params) {
		_log("Generated", sql, params);
	}

	public static void logNative(String sql, Object params) {
		_log("Native", sql, params);
	}

	private static void _log(String type, String sql, Object params) {
		if (log.isDebugEnabled()) {
			log.debug("{} SQL: {}", type, sql);
			if (params instanceof Object[]) {
				log.debug("SQL Parameters: {}", Lists.newArrayList((Object[]) params));
			} else {
				log.debug("SQL Parameters: {}", params);
			}
		}
	}
}
