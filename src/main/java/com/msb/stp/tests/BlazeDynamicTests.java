package com.msb.stp.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.msb.stp.aggregate.TestCaseData;
import com.msb.stp.repository.TestCaseRepository;
import com.msb.stp.utils.JsonUtil;
import com.msb.stp.utils.LeadEndpoints;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlazeDynamicTests {

	private static List<TestCaseData> testCases;

	// setter tĩnh để TestRunnerService truyền vào
	public static void setTestCases(List<TestCaseData> cases) {
		testCases = cases;
	}

	@TestFactory
	List<DynamicTest> blazeRunTestCases() {

		return testCases.stream()
				.map(tc -> DynamicTest.dynamicTest("API: [" + tc.getHttpMethod() + "] " + tc.getApiEndpoint(), () -> {
					String finalBody = JsonUtil.mergeRequestBody(tc.getBaseRequestBody(), tc.getRequestBody());
					Response response = LeadEndpoints.sendRequest(tc.getHttpMethod(), tc.getApiEndpoint(), finalBody,
							tc.getHeaders(), tc.getParams(), tc.getBaseUrl());

					Allure.addAttachment("Expected Status", String.valueOf(tc.getExpectedStatus()));
					Allure.addAttachment("Response Status", String.valueOf(response.getStatusCode()));
					Allure.addAttachment("Response Body", response.getBody().asPrettyString());

					Assertions.assertEquals(tc.getExpectedStatus(), response.getStatusCode());
					JsonUtil.validateResponse(response.getBody().asString(), tc.getExpectedResponse());
				})).toList();
	}
}
