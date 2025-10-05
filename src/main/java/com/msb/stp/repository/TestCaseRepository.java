package com.msb.stp.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.msb.stp.aggregate.TestCaseData;

@Repository
public class TestCaseRepository {

    private static JdbcTemplate staticJdbcTemplate;

    // Constructor được Spring gọi khi khởi tạo Bean
    public TestCaseRepository(JdbcTemplate jdbcTemplate) {
        staticJdbcTemplate = jdbcTemplate;
    }

    // Static method có thể gọi từ bất kỳ đâu (kể cả JUnit test class)
    public List<TestCaseData> getTestCasesByEnv(String envName) {
        String sql = """
            SELECT tc.case_id AS caseId,
                   tc.api_endpoint AS apiEndpoint,
                   tc.http_method AS httpMethod,
                   tc.request_body::text AS requestBody,
                   tc.expected_status AS expectedStatus,
                   tc.expected_response::text AS expectedResponse,
                   ts.base_url AS baseUrl,
                   ts.base_request_body AS baseRequestBody,
                   ts.env_name AS envName
            FROM stp_autotest.test_case tc
            JOIN stp_autotest.test_suite ts ON tc.suite_id = ts.suite_id
            WHERE ts.env_name = ?
            ORDER BY tc.case_id
            """;

        return staticJdbcTemplate.query(sql, new Object[]{envName}, (rs, rowNum) -> {
            TestCaseData tc = new TestCaseData();
            tc.setCaseId(rs.getLong("caseId"));
            tc.setApiEndpoint(rs.getString("apiEndpoint"));
            tc.setHttpMethod(rs.getString("httpMethod"));
            tc.setRequestBody(rs.getString("requestBody"));
            tc.setExpectedStatus(rs.getInt("expectedStatus"));
            tc.setExpectedResponse(rs.getString("expectedResponse"));
            tc.setBaseUrl(rs.getString("baseUrl"));
            tc.setBaseRequestBody(rs.getString("baseRequestBody"));
            return tc;
        });
    }
}
