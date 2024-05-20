package com.study.diploma.repo;

import com.study.diploma.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookRepo extends CrudRepository<Book, Long> {
    @Override
    Iterable<Book> findAll();

    @Query(value = "select available from books where name = ?1 and authors = ?2", nativeQuery = true)
    boolean isAvailable(String bookName, String authors);

    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.name = :name, b.authors = :authors, b.publication = :publication, b.isbn = :isbn, b.givenBy = :given_by WHERE b.id = :id")
    void updateBook(String name, String authors, int publication, String isbn, String given_by, Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.available = :available WHERE b.id = :id")
    void updateAvailable(boolean available, Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.rating = :rating WHERE b.id = :id")
    void updateRating(Double rating, Long id);

    @Override
    Optional<Book> findById(Long id);
}
