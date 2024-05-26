package com.study.diploma.repo;

import com.study.diploma.entity.Librarian;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LibrarianRepo extends CrudRepository<Librarian, Long> {
    @Query(value = "select * from librarians where email = ?1", nativeQuery = true)
    Optional<Librarian> findByEmail(String email);

    @Override
    void deleteById(Long aLong);

    @Transactional
    @Modifying
    @Query(value = "delete from librarians where email = ?1", nativeQuery = true)
    void deleteLibrarianByEmail(String email);

    @Query(value = "select * from librarians where email = ?1", nativeQuery = true)
    Optional<Librarian> existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Librarian l SET l.email = :email, l.password = :password WHERE l.id = :id")
    void updateLibrarianEmailAndPasswordById(String email, String password, Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Librarian l SET l.email = :email WHERE l.id = :id")
    void updateLibrarianEmailAndPasswordById(String email, Long id);

    @Override
    Optional<Librarian> findById(Long aLong);
}
