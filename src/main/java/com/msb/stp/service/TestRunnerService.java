package com.msb.stp.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.stereotype.Service;

import com.msb.stp.aggregate.TestCaseData;
import com.msb.stp.repository.TestCaseRepository;
import com.msb.stp.tests.BlazeDynamicTests;

import io.qameta.allure.junitplatform.AllureJunitPlatform;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestRunnerService {
	
	private final TestCaseRepository testCaseRepository;

	@SneakyThrows
	public String runTests(String user, String suite) {
	    String runId = user + "-" + UUID.randomUUID();

	    // 1. Query test cases từ DB
	    List<TestCaseData> testCases = testCaseRepository.getTestCasesByEnv(suite);
	    log.info("testCases = {}", testCases.size());

	    // 2. Truyền vào BlazeDynamicTests
	    BlazeDynamicTests.setTestCases(testCases);

	    // 3. Set allure-results dir
	    Path resultsDir = Paths.get("allure-results", runId);
	    Files.createDirectories(resultsDir);
	    System.setProperty("allure.results.directory", resultsDir.toString());

	    // 4. Run JUnit + gắn Allure listener
	    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
	            .selectors(DiscoverySelectors.selectClass(BlazeDynamicTests.class))
	            .build();

	    Launcher launcher = LauncherFactory.create();
	    launcher.registerTestExecutionListeners(new io.qameta.allure.junitplatform.AllureJunitPlatform());
	    launcher.execute(request);

	    // 5. Generate report
	    Path reportDir = Paths.get("reports", runId);
	    Files.createDirectories(reportDir);

	    String allureExecutable = "C:\\Users\\hunglq10\\Downloads\\allure-2.35.1\\allure-2.35.1\\bin\\allure.bat";
	    Process process = new ProcessBuilder(
	            allureExecutable, "generate",
	            resultsDir.toString(),
	            "-o", reportDir.toString(),
	            "--clean"
	    ).inheritIO().start();

	    process.waitFor();
	    return runId;
	}
}