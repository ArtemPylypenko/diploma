package com.study.diploma.services;

import com.study.diploma.entity.History;
import com.study.diploma.repo.BookRepo;
import com.study.diploma.repo.HistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class HistoryService implements ClassicalDao<History> {

    private final HistoryRepo historyRepo;
    private final BookRepo bookRepo;

    @Override
    public History save(History history) {
        return historyRepo.save(history);
    }

    @Override
    public void delete(History history) {
        historyRepo.delete(history);
    }

    @Override
    public List<History> getAll() {
        return StreamSupport.stream(historyRepo.findAll().spliterator(), false).toList();
    }

    public void updateReturn(Double rating, Long readerId, Long bookID, String comment) {
        historyRepo.updateReturn(readerId, bookID, comment, rating);
    }

    public void updateGivenAt(Long readerId, Long bookId) {
        historyRepo.updateGivenAt(readerId, bookId);
    }

    public Double getAVGRating(Long bookId) {
        Double avgRating = historyRepo.getAVGRating(bookId);
        if (avgRating == null)
            return null;
        else
            return (double) Math.round(avgRating * 10) / 10;
    }

    public void updateReturnAt(Long readerId, Long bookId) {
        historyRepo.updateReturnAt(readerId, bookId);
    }

    public List<History> getReaderHistory(Long reader) {
        return historyRepo.getAllByReader(reader);
    }

    public List<History> getHistoryAfter(LocalDateTime date, Long reader) {
        return historyRepo.getAllByReader(reader).stream().filter(h -> h.getCreatedAt().isAfter(date)).toList();
    }

    public List<History> getHistoryBefore(LocalDateTime date, Long reader) {
        return historyRepo.getAllByReader(reader).stream().filter(h -> h.getCreatedAt().isBefore(date)).toList();
    }

    public List<History> getHistoryBetween(LocalDateTime start, LocalDateTime end, Long reader) {
        return historyRepo.getAllByReader(reader).stream().filter(h ->
                h.getCreatedAt().isAfter(start) && h.getCreatedAt().isBefore(end)).toList();
    }

    public List<History> getAllByReader(Long reader) {
        return historyRepo.getAllByReader(reader);
    }

    public List<History> getAllByBook(Long book) {
        return historyRepo.getAllByBook(book);
    }

    public List<History> getAllNotReturned() {
        return historyRepo.getAllNotReturned();
    }

    public void setCanceled(Long readerId, Long bookId) {
        historyRepo.setCanceled(readerId, bookId);
    }

    public List<History> getCanceled() {
        return historyRepo.getCanceled();
    }

    public List<History> getAllReturned() {
        return historyRepo.getAllReturned();
    }
}
