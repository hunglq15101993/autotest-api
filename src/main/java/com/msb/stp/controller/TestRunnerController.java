package com.msb.stp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msb.stp.service.TestRunnerService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestRunnerController {

	private final TestRunnerService testRunnerService;

	@PostMapping("/run-tests")
	@SneakyThrows
	public ResponseEntity<Map<String, String>> runTests(@RequestParam String user, @RequestParam String suite) {
		String runId = testRunnerService.runTests(user, suite);
		String reportUrl = "http://localhost:8080/reports/" + runId + "/index.html";

		return ResponseEntity.ok(Map.of("runId", runId, "reportUrl", reportUrl));
	}
}
