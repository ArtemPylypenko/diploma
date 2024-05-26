package com.study.diploma.controllers;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.History;
import com.study.diploma.services.BookService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/excel-reports")
public class ExcelReportController {

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private BookService bookService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ReaderService readerService;


    // /excel-reports/books-not-returned
    @GetMapping("/books-not-returned")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public ResponseEntity<byte[]> downloadBooksNotReturnedReport() {
        try {
            List<History> history = historyService.getAllNotReturned();
            Map<Book, String> reportMap = new HashMap<>();
            history.forEach(h -> reportMap.put(bookService.getById(h.getBook()).get(), readerService.getById(h.getReader()).get().getName()));
            byte[] report = excelReportService.generateBooksNotReturnedReport(reportMap);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books_not_returned.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // /excel-reports/books-popularity
    @GetMapping("/books-popularity")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public ResponseEntity<byte[]> downloadBooksPopularityReport() {
        try {
            List<Book> books = bookService.getAllByRating();
            byte[] report = excelReportService.generateBooksPopularityReport(books);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books_popularity.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
//
//    @GetMapping("/readers-unreturned-books")
//    public ResponseEntity<byte[]> downloadReadersWithBooksNotReturnedReport() {
//        try {
//            List<Reader> readers = readerService.getReadersWithBooksNotReturned();
//            byte[] report = excelReportService.generateReadersWithBooksNotReturnedReport(readers);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=readers_unreturned_books.xlsx")
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(report);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
}

