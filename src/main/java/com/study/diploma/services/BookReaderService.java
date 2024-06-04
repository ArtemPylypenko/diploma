package com.study.diploma.services;


import com.study.diploma.entity.Book;
import com.study.diploma.entity.BookReader;
import com.study.diploma.entity.History;
import com.study.diploma.entity.Reader;
import com.study.diploma.repo.BookReaderRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReaderService {
    private final BookReaderRepo bookReaderRepo;
    private final BookService bookService;
    private final ReaderService readerService;
    private final HistoryService historyService;

    public boolean existsByBookAndUser(Long book, Long user) {
        return bookReaderRepo.existsBookUser(book, user).isPresent();
    }


    @Transactional
    public void addReaderBook(Reader reader, Book book) {

        reader.getBooks().add(book);
        book.getReaders().add(reader);

        History history = new History();
        history.setBook(book.getId());
        history.setReader(reader.getId());
        history.setCreatedAt(LocalDateTime.now());
        updateCreateTime(book.getId(), reader.getId());
        historyService.save(history);
//        bookHistoryRepo.save(bookHistory);
    }

    public List<Book> getReservedBooks(Long readerId) {
        return bookReaderRepo.getReservedBooks(readerId);
    }

    public void updateCreateTime(Long book, Long reader) {
        bookReaderRepo.updateCreateTime(bookService.getById(book).get(), readerService.getById(reader).get());
    }

    public void updateGiveTime(Long id) {
        bookReaderRepo.updateGiveTime(id);
        historyService.updateGivenAt(bookReaderRepo.getById(id).getReader().getId(), bookReaderRepo.getById(id).getBook().getId());
    }

    public void updateReturnTime(Long book, Long reader) {
        bookReaderRepo.updateReturnTime(bookService.getById(book).get(), readerService.getById(reader).get());
    }

    public void deleteReaderBook(Reader reader, Book book) {
        bookReaderRepo.deleteReaderBook(reader, book);
    }

    public void deleteReaderBook(Long reader, Long book) {
        bookReaderRepo.deleteReaderBook(reader, book);
        bookService.increaseAvailable(book);
    }

    public void deleteReaderBook(Long id) {
        bookReaderRepo.deleteReaderBook(id);
    }
    public void cancelReaderBook(Long id) {
        historyService.setCanceled(getById(id).getReader().getId(),getById(id).getBook().getId());
        deleteReaderBook(id);
    }

    public Double getAVGBookRating(Book book) {
        return bookReaderRepo.getAvgRating(book);
    }

    @Transactional
    public void deleteUserBooksByBook(Book book) {
        bookReaderRepo.deleteByBook(book);
    }

    @Transactional
    public void deleteUserBooksByUser(Reader reader) {
        bookReaderRepo.deleteByReader(reader);
    }

    public BookReader getById(Long id) {
        return bookReaderRepo.getById(id);
    }

    public List<Book> getReadersBooks(Long readerId) {
        return bookReaderRepo.getReadersBooks(readerId);
    }
}
