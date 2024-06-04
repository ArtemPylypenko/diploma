package com.study.diploma.repo;

import com.study.diploma.entity.History;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryRepo extends CrudRepository<History, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.returnedAt = CURRENT_TIMESTAMP, h.comment = :comment, h.rating = :rating WHERE h.reader = :reader and h.book = :book and h.returnedAt is NULL")
    void updateReturn(Long reader, Long book, String comment, Double rating);

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.rating = :rating WHERE h.reader = :reader and h.book = :book and h.returnedAt is null")
    void updateRating(Double rating, Long reader, Long book);

    @Query(value = "SELECT AVG(rating) from history where book_id = :bookId", nativeQuery = true)
    Double getAVGRating(Long bookId);

    @Query(value = "SELECT * from history where reader_id = :reader", nativeQuery = true)
    List<History> getAllByReader(Long reader);

    @Query(value = "SELECT * from history where book_id = :book", nativeQuery = true)
    List<History> getAllByBook(Long book);

    @Query(value = "SELECT * from history where returned_at is null", nativeQuery = true)
    List<History> getAllNotReturned();

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.givenAt = CURRENT_TIMESTAMP WHERE h.reader = :readerId and h.book = :bookId and h.returnedAt is null")
    void updateGivenAt(Long readerId, Long bookId);

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.isCanceled = true WHERE h.reader = :readerId and h.book = :bookId and h.givenAt is null")
    void setCanceled(Long readerId, Long bookId);

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.returnedAt = CURRENT_TIMESTAMP WHERE h.reader = :readerId and h.book = :bookId and h.returnedAt is null")
    void updateReturnAt(Long readerId, Long bookId);
    @Query(value = "SELECT * from history where is_canceled = true", nativeQuery = true)
    List<History> getCanceled();

    @Query(value = "SELECT * from history where returned_at is not null", nativeQuery = true)
    List<History> getAllReturned();
}
