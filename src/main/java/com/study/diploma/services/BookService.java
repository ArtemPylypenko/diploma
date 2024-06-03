package com.study.diploma.services;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.BookReader;
import com.study.diploma.repo.BookReaderRepo;
import com.study.diploma.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class BookService implements ClassicalDao<Book> {
    private final BookRepo bookRepo;
    private final BookReaderRepo bookReaderRepo;

    @Override
    public Book save(Book book) {
        return bookRepo.save(book);
    }

    @Override
    public void delete(Book book) {
        bookReaderRepo.deleteByBook(book);
        bookRepo.deleteById(book.getId());
    }

    @Override
    public List<Book> getAll() {
        return StreamSupport.stream(bookRepo.findAll().spliterator(), false).toList();
    }

    public Page<Book> getAllBooksPage(PageRequest page) {
        return bookRepo.findAllBooks(page);
    }

    public Optional<Book> getById(Long id) {
        return bookRepo.findById(id);
    }

    public void deleteById(Long id) {
        delete(getById(id).get());
    }

    public void updateById(String name, String authors, int publication, String isbn, String given_by, Long id, Integer exemplars) {
        bookRepo.updateBook(name, authors, publication, isbn, given_by, id, exemplars, exemplars);
    }

    public void increaseAvailable(Long id) {
        bookRepo.increaseAvailable(id);
    }

    public void decreaseAvailable(Long id) {
        bookRepo.decreaseAvailable(id);
    }

    public void updateRating(Double rating, Long id) {
        bookRepo.updateRating(rating, id);
    }

    public List<Book> getAllByRating() {
        return bookRepo.getBooksByRating();
    }

    public List<Book> getByName(String name) {
        return bookRepo.getBookByName(name);
    }

    public List<Book> getByGenres(String genres) {
        return bookRepo.getBookByGenres(genres);
    }

    public Page<Book> findAll(PageRequest page) {
        return bookRepo.findAllBooks(page);
    }

    public Collection<? extends Book> getByAuthors(String name) {
        return bookRepo.getByAuthors(name);
    }

    public Page<BookReader> getReservedBooks(PageRequest of) {
        return bookReaderRepo.getReservedBooks(of);
    }

    public Page<BookReader> getGivenBooks(PageRequest of) {
        return bookReaderRepo.getGivenBooks(of);
    }
}
