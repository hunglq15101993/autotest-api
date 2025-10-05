package com.msb.stp.service;

import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class ReportService {

    public String getReportUrl(String user, String runId) {
        // Giả định bạn sẽ mount folder này qua Nginx hoặc serve tĩnh
        return "/reports/" + user + "/" + runId + "/index.html";
    }
}
