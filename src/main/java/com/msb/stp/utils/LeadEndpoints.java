package com.msb.stp.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.msb.stp.aggregate.TestCaseData;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * LeadEndpoints class để cấu hình RestAssured cho API tests
 */
@Slf4j
public class LeadEndpoints {

	public static Response sendRequest(String method, String endpoint, Object body,
			List<TestCaseData.Header> headersList, List<TestCaseData.Param> paramsList, String baseUrl) {

		var request = RestAssured.given().baseUri(baseUrl);

// headers
		Map<String, String> headers = (headersList == null || headersList.isEmpty()) ? Collections.emptyMap()
				: headersList.stream().collect(
						Collectors.toMap(TestCaseData.Header::getKey, TestCaseData.Header::getValue, (a, b) -> b));
		if (!headers.isEmpty()) {
			request.headers(headers);
		}

// query params
		Map<String, String> params = (paramsList == null || paramsList.isEmpty()) ? Collections.emptyMap()
				: paramsList.stream().collect(
						Collectors.toMap(TestCaseData.Param::getKey, TestCaseData.Param::getValue, (a, b) -> b));
		if (!params.isEmpty()) {
			request.queryParams(params);
		}

// body
		if (body != null && (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")
				|| method.equalsIgnoreCase("PATCH"))) {
			request.contentType("application/json; charset=UTF-8").accept("application/json").body(body);
		}

		String curl = CurlUtil.buildCurl(method, baseUrl + endpoint, headers, params, body);
		Allure.addAttachment("cURL Request", new ByteArrayInputStream(curl.getBytes(StandardCharsets.UTF_8)));
// send
		return switch (method.toUpperCase()) {
		case "GET" -> request.when().get(endpoint);
		case "POST" -> request.when().post(endpoint);
		case "PUT" -> request.when().put(endpoint);
		case "PATCH" -> request.when().patch(endpoint);
		case "DELETE" -> request.when().delete(endpoint);
		default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
		};
	}

}