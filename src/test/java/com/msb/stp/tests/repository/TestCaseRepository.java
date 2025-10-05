package com.msb.stp.tests.repository;

import com.msb.stp.tests.config.DbConfig;
import com.msb.stp.tests.payload.TestCaseData;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TestCaseRepository {

	@SneakyThrows
	public static List<TestCaseData> getTestCasesByEnv(String envName) {
	    String sql = """
	        SELECT 
	            tc.case_id      AS caseId,
	            tc.api_endpoint AS apiEndpoint,
	            tc.http_method  AS httpMethod,
	            tc.request_body::text AS requestBody,
	            tc.expected_status    AS expectedStatus,
	            tc.expected_response::text AS expectedResponse,
	            tc.description AS description,
	            ts.base_url     AS baseUrl,
	            ts.base_request_body     AS baseRequestBody,
	            ts.env_name     AS envName,
	            th.header_key   AS headerKey,
	            th.header_value AS headerValue,
	            tp.param_key    AS paramKey,
	            tp.param_value  AS paramValue
	        FROM stp_autotest.test_case tc
	        JOIN stp_autotest.test_suite ts ON tc.suite_id = ts.suite_id
	        LEFT JOIN stp_autotest.test_suite_header th ON ts.suite_id = th.suite_id
	        LEFT JOIN stp_autotest.test_suite_param tp ON ts.suite_id = tp.suite_id
	        WHERE ts.env_name = ?
	        ORDER BY tc.case_id;
	    """;

	    Map<Long, TestCaseData> caseMap = new LinkedHashMap<>();

	    try (Connection conn = DbConfig.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, envName);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Long caseId = rs.getLong("caseId");

	                TestCaseData testCase = caseMap.get(caseId);
	                if (testCase == null) {
	                    testCase = new TestCaseData();
	                    testCase.setCaseId(caseId);
	                    testCase.setBaseRequestBody(rs.getString("baseRequestBody"));
	                    testCase.setApiEndpoint(rs.getString("apiEndpoint"));
	                    testCase.setHttpMethod(rs.getString("httpMethod"));
	                    testCase.setRequestBody(rs.getString("requestBody"));
	                    testCase.setExpectedStatus(rs.getInt("expectedStatus"));
	                    testCase.setExpectedResponse(rs.getString("expectedResponse"));
	                    testCase.setBaseUrl(rs.getString("baseUrl"));
	                    testCase.setDescription(rs.getString("description"));
	                    testCase.setHeaders(new ArrayList<>());
	                    testCase.setParams(new ArrayList<>());

	                    caseMap.put(caseId, testCase);
	                }

	                // headers (theo suite)
	                String headerKey = rs.getString("headerKey");
	                String headerValue = rs.getString("headerValue");
	                if (headerKey != null && headerValue != null) {
	                    testCase.getHeaders().add(new TestCaseData.Header(headerKey, headerValue));
	                }

	                // params (theo suite)
	                String paramKey = rs.getString("paramKey");
	                String paramValue = rs.getString("paramValue");
	                if (paramKey != null && paramValue != null) {
	                    testCase.getParams().add(new TestCaseData.Param(paramKey, paramValue));
	                }
	            }
	        }
	    }

	    return new ArrayList<>(caseMap.values());
	}


}
