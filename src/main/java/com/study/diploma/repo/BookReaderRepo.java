package com.study.diploma.repo;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.BookReader;
import com.study.diploma.entity.Reader;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookReaderRepo extends CrudRepository<BookReader, Long> {

    @Query(value = "select * from book_reader br where br.book_id = :book and br.reader_id = :reader", nativeQuery = true)
    Optional<BookReader> existsBookUser(Long book, Long reader);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.book = :book")
    void deleteByBook(Book book);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.reader = :reader")
    void deleteByReader(Reader reader);

    @Modifying
    @Transactional
    @Query("UPDATE BookReader bh SET bh.returnedAt = CURRENT_TIMESTAMP  WHERE bh.book = :book and bh.reader = :reader")
    void updateReturnTime(Book book, Reader reader);

    @Modifying
    @Transactional
    @Query("UPDATE BookReader bh SET bh.createdAt = CURRENT_TIMESTAMP  WHERE bh.book = :book and bh.reader = :reader")
    void updateCreateTime(Book book, Reader reader);

    @Query("SELECT AVG(e.rating) FROM BookReader e where e.book = :book")
    Double getAvgRating(Book book);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.reader = :reader and bh.book = :book")
    void deleteReaderBook(Reader reader, Book book);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.id = :id")
    void deleteReaderBook(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.reader.id = :reader and bh.book.id = :book")
    void deleteReaderBook(Long reader, Long book);

    @Query(value = "SELECT br.book FROM BookReader br WHERE br.reader.id = :readerId AND br.givenAt IS NULL")
    List<Book> getReservedBooks(Long readerId);


    @Query(value = "SELECT br FROM BookReader br WHERE br.givenAt IS NULL")
    Page<BookReader> getReservedBooks(PageRequest of);

    @Query(value = "SELECT br FROM BookReader br WHERE br.givenAt IS NOT NULL")
    Page<BookReader> getGivenBooks(PageRequest of);

    @Modifying
    @Transactional
    @Query("UPDATE BookReader br SET br.givenAt = CURRENT_TIMESTAMP  WHERE br.id = :id")
    void updateGiveTime(Long id);

    @Query(value = "SELECT br FROM BookReader br WHERE br.id = :id")
    BookReader getById(Long id);

    @Query(value = "SELECT br.book FROM BookReader br WHERE br.reader.id = :id AND br.givenAt IS NOT NULL")
    List<Book> getReadersBooks(Long id);
}
