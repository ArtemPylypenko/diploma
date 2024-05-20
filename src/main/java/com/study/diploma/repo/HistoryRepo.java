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
    @Query("UPDATE History h SET h.rating = :rating WHERE h.reader = :reader and h.book = :book and h.returnedAt = null")
    void updateRating(Double rating, Long reader, Long book);

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.returnedAt = CURRENT_TIMESTAMP WHERE h.reader = :reader and h.book = :book")
    void updateReturn(Long reader, Long book);

    @Query(value = "SELECT AVG(rating) from history where book_id = :bookId", nativeQuery = true)
    Double getAVGRating(Long bookId);

    @Query(value = "SELECT * from history where reader_id = :reader", nativeQuery = true)
    List<History> getAllByReader(Long reader);
}
