package com.msb.stp.tests.tests;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;

import com.msb.stp.tests.endpoints.LeadEndpoints;
import com.msb.stp.tests.payload.TestCaseData;
import com.msb.stp.tests.repository.TestCaseRepository;
import com.msb.stp.tests.utils.JsonUtil;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class BlazeApiTest {

	private static List<TestCaseData> testCases;

	@BeforeAll
	public static void setup() {
		String env = System.getProperty("env", "SIT");
		testCases = TestCaseRepository.getTestCasesByEnv(env);
	}

	@TestFactory
	@DisplayName("Blaze API Tests")
	List<DynamicTest> blazeRunTestCases() {
		return testCases.stream()
				.map(tc -> DynamicTest.dynamicTest("Desctiption: [" + tc.getDescription() + "] " + tc.getApiEndpoint(), () -> {
					Allure.addAttachment("Expected Request", tc.getRequestBody());
					String finalBody = JsonUtil.mergeRequestBody(tc.getBaseRequestBody(), tc.getRequestBody());
					Response response = LeadEndpoints.sendRequest(tc.getHttpMethod(), tc.getApiEndpoint(), finalBody,
							tc.getHeaders(), tc.getParams(), tc.getBaseUrl() // bổ sung tham số baseUrl
					);
					
					Allure.addAttachment("Expected Response", tc.getExpectedResponse());
					Allure.addAttachment("Actual Response", new ByteArrayInputStream(response.getBody().asPrettyString().getBytes(StandardCharsets.UTF_8)));

					JsonUtil.validateResponse(response.getBody().asString(), tc.getExpectedResponse());
				})).toList();
	}

}