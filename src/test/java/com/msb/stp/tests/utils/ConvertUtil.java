package com.msb.stp.tests.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String toString(Object o) {

		try {
			mapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String toStringNonNull(Object o) {

		try {
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static <T> T toObject(String str, Class<T> clazz) {
		try {
			mapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
			return mapper.readValue(str, clazz);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static <T> T convertValueObject(Object o, Class<T> clazz) {
		try {
			mapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
			String str = mapper.writeValueAsString(o);
			return mapper.readValue(str, clazz);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String trimString(String str) {
		if (ObjectUtils.isEmpty(str)) {
			return str;
		}

		return str.trim();
	}

}

