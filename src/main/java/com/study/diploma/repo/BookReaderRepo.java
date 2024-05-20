package com.study.diploma.repo;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.BookReader;
import com.study.diploma.entity.Reader;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookReaderRepo extends CrudRepository<BookReader, Long> {


    @Query(value = "select * from book_reader where book_id = ?1 and reader_id = ?2", nativeQuery = true)
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
    @Query("UPDATE BookReader bh SET bh.updatedAt = CURRENT_TIMESTAMP  WHERE bh.book = :book and bh.reader = :reader")
    void updateReturnTime(Book book, Reader reader);

    @Modifying
    @Transactional
    @Query("UPDATE BookReader bh SET bh.createdAt = CURRENT_TIMESTAMP  WHERE bh.book = :book and bh.reader = :reader")
    void updateGiveTime(Book book, Reader reader);


    @Query("SELECT AVG(e.rating) FROM BookReader e where e.book = :book")
    Double getAvgRating(Book book);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookReader bh WHERE bh.reader = :reader and bh.book = :book")
    void deleteReaderBook(Reader reader, Book book);
}
