package com.study.diploma.controllers;

import com.study.diploma.entity.History;
import com.study.diploma.services.ExcelReportService;
import com.study.diploma.services.HistoryService;
import com.study.diploma.services.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel-reports")
public class ExcelReportController {

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ReaderService readerService;

    @GetMapping("/books-canceled")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public ResponseEntity<byte[]> downloadCanceled() {
        try {
            List<History> history = historyService.getCanceled();
            byte[] report = excelReportService.generateCanceledReport(history);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations_canceled.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/books-history")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public ResponseEntity<byte[]> downloadHistory() {
        try {
            List<History> history = historyService.getAllReturned();
            byte[] report = excelReportService.generateBookHistoryReport(history);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books_history.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

