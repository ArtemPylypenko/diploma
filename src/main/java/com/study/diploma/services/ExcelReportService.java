package com.study.diploma.services;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.History;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelReportService {
    private final ReaderService readerService;
    private final BookService bookService;

    public byte[] generateBooksNotReturnedReport(Map<Book, String> booksWithReaders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books Not Returned");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Book Name");
        headerRow.createCell(1).setCellValue("Authors");
        headerRow.createCell(2).setCellValue("Reader");

        int rowNum = 1;
        for (Map.Entry<Book, String> entry : booksWithReaders.entrySet()) {
            Book book = entry.getKey();
            String readerName = entry.getValue();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getName());
            row.createCell(1).setCellValue(book.getAuthors());
            row.createCell(2).setCellValue(readerName);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public byte[] generateCanceledReport(List<History> history) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Canceled reservation");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Surname");
        headerRow.createCell(2).setCellValue("Book");
        headerRow.createCell(3).setCellValue("Date");

        int rowNum = 1;
        for (History h : history) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(readerService.getById(h.getReader()).get().getName());
            row.createCell(1).setCellValue(readerService.getById(h.getReader()).get().getSurname());
            row.createCell(2).setCellValue(bookService.getById(h.getBook()).get().getName());
            row.createCell(3).setCellValue(h.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public byte[] generateBookHistoryReport(List<History> history) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Canceled reservation");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Surname");
        headerRow.createCell(2).setCellValue("Book");
        headerRow.createCell(3).setCellValue("Given");
        headerRow.createCell(4).setCellValue("Returned");

        int rowNum = 1;
        for (History h : history) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(readerService.getById(h.getReader()).get().getName());
            row.createCell(1).setCellValue(readerService.getById(h.getReader()).get().getSurname());
            row.createCell(2).setCellValue(bookService.getById(h.getBook()).get().getName());
            row.createCell(3).setCellValue(h.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            row.createCell(4).setCellValue(h.getReturnedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

}
