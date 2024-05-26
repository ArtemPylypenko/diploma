package com.study.diploma.services;

import com.study.diploma.dto.HistoryDTO;
import com.study.diploma.entity.History;
import com.study.diploma.repo.BookRepo;
import com.study.diploma.repo.HistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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

    public Double getAVGRating(Long bookId) {
        Double avgRating = historyRepo.getAVGRating(bookId);
        if (avgRating == null)
            return null;
        else
            return (double) Math.round(avgRating * 10) / 10;
    }

    public void updateReturn() {
        StreamSupport.stream(bookRepo.findAll().spliterator(), false).toList().forEach(book -> {
            if (getAVGRating(book.getId()) != null)
                bookRepo.updateRating(getAVGRating(book.getId()), book.getId());
        });
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

    public HistoryDTO historyToDto(History history) {
        String name = bookRepo.findById(history.getBook()).get().getName();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd HH:mm", Locale.US);

        // Перетворюємо LocalDateTime у String

        return new HistoryDTO(name, history.getCreatedAt().format(formatter), history.getReturnedAt().format(formatter));
    }

    public List<History> getAllNotReturned() {
        return historyRepo.getAllNotReturned();
    }
}
