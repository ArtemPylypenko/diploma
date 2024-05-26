package com.study.diploma.services;


import com.study.diploma.entity.Book;
import com.study.diploma.entity.History;
import com.study.diploma.entity.Reader;
import com.study.diploma.repo.BookReaderRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookReaderService {
    private final BookReaderRepo bookHistoryRepo;
    private final BookService bookService;
    private final ReaderService readerService;
    private final HistoryService historyService;

    public boolean existsByBookAndUser(Long book, Long user) {
        return bookHistoryRepo.existsBookUser(book, user).isPresent();
    }


    @Transactional
    public void addReaderBook(Reader reader, Book book) {

        reader.getBooks().add(book);
        book.getReaders().add(reader);

        History history = new History();
        history.setBook(book.getId());
        history.setReader(reader.getId());
        history.setCreatedAt(LocalDateTime.now());
        historyService.save(history);
//        bookHistoryRepo.save(bookHistory);
    }

    public void updateRiveTime(Long book, Long reader) {
        bookHistoryRepo.updateGiveTime(bookService.getById(book).get(), readerService.getById(reader).get());
    }

    public void updateReturnTime(Long book, Long reader) {

        bookHistoryRepo.updateReturnTime(bookService.getById(book).get(), readerService.getById(reader).get());
    }

    public void deteleReaderBook(Reader reader, Book book) {
        bookHistoryRepo.deleteReaderBook(reader, book);
    }

    public Double getAVGBookRating(Book book) {
        return bookHistoryRepo.getAvgRating(book);
    }

    @Transactional
    public void deleteUserBooksByBook(Book book) {
        bookHistoryRepo.deleteByBook(book);
    }

    @Transactional
    public void deleteUserBooksByUser(Reader reader) {
        bookHistoryRepo.deleteByReader(reader);
    }

}
