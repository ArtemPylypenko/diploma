package com.study.diploma.services;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.Reader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ExcelReportService {

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

    public byte[] generateBooksPopularityReport(List<Book> books) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books by Popularity");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Authors");
        headerRow.createCell(2).setCellValue("Rating");

        int rowNum = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getName());
            row.createCell(1).setCellValue(book.getAuthors());
            row.createCell(2).setCellValue(book.getRating());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public byte[] generateReadersWithBooksNotReturnedReport(List<Reader> readers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Readers with Unreturned Books");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Surname");
        headerRow.createCell(2).setCellValue("Phone");

        int rowNum = 1;
        for (Reader reader : readers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(reader.getName());
            row.createCell(1).setCellValue(reader.getSurname());
            row.createCell(2).setCellValue(reader.getPhone());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
