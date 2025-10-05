package com.msb.stp.aggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseData {
    private Long caseId;
    private String apiEndpoint;
    private String baseRequestBody;
    private String httpMethod;
    private String requestBody;
    private Integer expectedStatus;
    private String expectedResponse;
    private String description;

    private String baseUrl;   // lấy từ test_environment để gán RestAssured

    private List<Header> headers = new ArrayList<>();
    private List<Param> params = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        private String key;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param {
        private String key;
        private String value;
    }
}
