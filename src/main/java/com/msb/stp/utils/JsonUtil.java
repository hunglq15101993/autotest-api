package com.msb.stp.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JsonUtil class hỗ trợ: 1. Merge baseRequestBody + requestBody (dot-notation
 * keys). 2. Validate expectedResponse (dot-notation keys) trên actual response.
 */
public class JsonUtil {

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Merge baseRequestBody với requestBody (dot-notation).
	 * 
	 * @param baseJson      JSON gốc (full body)
	 * @param overridesJson JSON chứa các trường cần override (key có thể là
	 *                      dot-notation)
	 * @return JSON string sau khi merge
	 */
	public static String mergeRequestBody(String baseJson, String overridesJson) throws JsonProcessingException {
		if (baseJson == null || baseJson.isBlank()) {
			throw new IllegalArgumentException("baseRequestBody cannot be null or empty");
		}

		ObjectNode root = (ObjectNode) mapper.readTree(baseJson);

		if (overridesJson != null && !overridesJson.isBlank()) {
			Map<String, Object> overrides = mapper.readValue(overridesJson, new TypeReference<>() {
			});
			overrides.forEach((path, value) -> setByPath(root, path, value));
		}
		return mapper.writeValueAsString(root);
	}

	/**
	 * Validate expectedResponse với actualResponse (partial assert).
	 * 
	 * @param responseJson response thực tế (full JSON)
	 * @param expectedJson JSON chứa expected fields (dot-notation)
	 */
	public static void validateResponse(String responseJson, String expectedJson) throws JsonProcessingException {
	    if (expectedJson == null || expectedJson.isBlank()) {
	        return; // không có gì để validate
	    }

	    JsonNode actual = mapper.readTree(responseJson);
	    Map<String, Object> expected = mapper.readValue(expectedJson, new TypeReference<>() {});

	    expected.forEach((path, expectedValue) -> {
	        boolean matched = checkPathFlexible(actual, path.split("\\."), 0, String.valueOf(expectedValue));

	        if (!matched) {
	            throw new AssertionError("Mismatch at " + path + " expected=" + expectedValue);
	        }
	    });
	}

	/**
	 * Đệ quy duyệt path, hỗ trợ cả object và array.
	 * Nếu path trỏ tới array mà không có [index] → duyệt hết phần tử.
	 * Nếu key cuối cùng → chỉ cần 1 phần tử match expected là PASS.
	 */
	private static boolean checkPathFlexible(JsonNode node, String[] parts, int idx, String expectedValue) {
	    if (node == null || node.isMissingNode()) return false;

	    String part = parts[idx];
	    String fieldName = part;
	    Integer index = null;

	    if (part.contains("[") && part.endsWith("]")) {
	        fieldName = part.substring(0, part.indexOf("["));
	        index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
	    }

	    JsonNode child = node.get(fieldName);
	    if (child == null || child.isMissingNode()) return false;

	    if (idx == parts.length - 1) {
	        // Bước cuối → so sánh giá trị
	        if (child.isArray() && index == null) {
	            for (JsonNode element : child) {
	                if (element.isValueNode() && element.asText().equals(expectedValue)) {
	                    return true;
	                }
	                if (element.isObject()) {
	                    // Nếu element là object, thử lấy fieldName cuối
	                    JsonNode val = element.get(fieldName);
	                    if (val != null && val.asText().equals(expectedValue)) {
	                        return true;
	                    }
	                }
	            }
	            return false;
	        } else if (child.isArray()) {
	            return index < child.size() && child.get(index).asText().equals(expectedValue);
	        } else {
	            return child.asText().equals(expectedValue);
	        }
	    }

	    // Chưa tới bước cuối
	    if (child.isArray() && index == null) {
	        for (JsonNode element : child) {
	            if (checkPathFlexible(element, parts, idx + 1, expectedValue)) {
	                return true;
	            }
	        }
	        return false;
	    } else if (child.isArray()) {
	        return index < child.size() && checkPathFlexible(child.get(index), parts, idx + 1, expectedValue);
	    } else {
	        return checkPathFlexible(child, parts, idx + 1, expectedValue);
	    }
	}


	private static Object normalizeValue(Object value) {
	    if (value instanceof String str) {
	        // Regex bắt format dd/MM/yyyy
	        if (str.matches("\\d{2}/\\d{2}/\\d{4}")) {
	            try {
	                DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	                DateTimeFormatter outputFmt = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
	                return LocalDate.parse(str, inputFmt).format(outputFmt);
	            } catch (Exception ignored) {}
	        }
	    }
	    return value;
	}


	private static void setByPath(ObjectNode root, String path, Object value) {
	    String[] parts = path.split("\\.");
	    JsonNode current = root;

	    for (int i = 0; i < parts.length - 1; i++) {
	        String part = parts[i];
	        String fieldName = part;
	        Integer index = null;

	        // nếu có dạng applicants[0]
	        if (part.contains("[") && part.endsWith("]")) {
	            fieldName = part.substring(0, part.indexOf("["));
	            index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
	        }

	        // lấy node con
	        if (current.isObject()) {
	            ObjectNode obj = (ObjectNode) current;
	            JsonNode child = obj.get(fieldName);

	            if (child == null || child.isNull()) {
	                // nếu có index thì tạo array, không thì tạo object
	                child = (index != null) ? mapper.createArrayNode() : mapper.createObjectNode();
	                obj.set(fieldName, child);
	            }

	            if (index != null) {
	                ArrayNode arr = (ArrayNode) child;
	                // đảm bảo array đủ size
	                while (arr.size() <= index) {
	                    arr.add(mapper.createObjectNode());
	                }
	                current = arr.get(index);
	            } else {
	                current = child;
	            }

	        } else if (current.isArray()) {
	            throw new IllegalArgumentException("Unexpected array in path at: " + part);
	        }
	    }

	    // set giá trị cuối
	    String lastPart = parts[parts.length - 1];
	    String fieldName = lastPart;
	    Integer index = null;

	    if (lastPart.contains("[") && lastPart.endsWith("]")) {
	        fieldName = lastPart.substring(0, lastPart.indexOf("["));
	        index = Integer.parseInt(lastPart.substring(lastPart.indexOf("[") + 1, lastPart.indexOf("]")));
	    }

	    if (current.isObject()) {
	        ObjectNode obj = (ObjectNode) current;
	        if (index == null) {
	            obj.set(fieldName, mapper.valueToTree(normalizeValue(value)));
	        } else {
	            ArrayNode arr = obj.withArray(fieldName);
	            while (arr.size() <= index) {
	                arr.add(mapper.nullNode());
	            }
	            arr.set(index, mapper.valueToTree(normalizeValue(value)));
	        }
	    }
	}



	public static JsonNode getByPath(JsonNode root, String path) {
	    String[] parts = path.split("\\.");
	    JsonNode current = root;

	    for (int i = 0; i < parts.length; i++) {
	        if (current == null || current.isMissingNode()) {
	            return null;
	        }

	        String part = parts[i];
	        String fieldName = part;
	        Integer index = null;

	        if (part.contains("[") && part.endsWith("]")) {
	            fieldName = part.substring(0, part.indexOf("["));
	            index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
	        }

	        // Nếu là bước cuối cùng -> return luôn current để validate xử lý key cuối
	        if (i == parts.length - 1) {
	            return current.get(fieldName);
	        }

	        current = current.get(fieldName);

	        if (current == null || current.isMissingNode()) {
	            return null;
	        }

	        if (current.isArray()) {
	            ArrayNode arr = (ArrayNode) current;
	            if (index == null) {
	                // Nếu không chỉ định index, mặc định lấy cả array luôn
	                return current;
	            } else if (arr.size() > index) {
	                current = arr.get(index);
	            } else {
	                return null;
	            }
	        }
	    }

	    return current;
	}




}