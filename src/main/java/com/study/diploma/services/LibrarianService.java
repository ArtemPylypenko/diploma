package com.study.diploma.services;

import com.study.diploma.entity.Librarian;
import com.study.diploma.repo.LibrarianRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LibrarianService implements ClassicalDao<Librarian> {
    private final LibrarianRepo librarianRepo;
    private final UserService userService;

    @Override
    public Librarian save(Librarian librarian) {
        userService.save(librarian);
        return librarianRepo.save(librarian);
    }

    @Override
    public void delete(Librarian librarian) {
        String email = librarian.getEmail();
        librarianRepo.deleteLibrarianByEmail(email);
        userService.deleteByEmail(email);
    }

    public void deleteById(Long id) {
        String email = getById(id).get().getEmail();
        deleteLibAndUser(email);
    }

    private void deleteLibAndUser(String email) {
        userService.deleteByEmail(email);
        deleteByEmail(email);
    }

    private void deleteByEmail(String email) {
        librarianRepo.deleteLibrarianByEmail(email);
    }

    @Override
    public List<Librarian> getAll() {
        return StreamSupport.stream(librarianRepo.findAll().spliterator(), false).toList();
    }

    public Optional<Librarian> getById(Long id) {
        return librarianRepo.findById(id);
    }

    public void updateById(String email, String password, Long id) {
        String oldEmail = librarianRepo.findById(id).get().getEmail();
        userService.updateByOldEmail(oldEmail, email, password);

        librarianRepo.updateLibrarianEmailAndPasswordById(email, password, id);
    }

    public boolean existByEmail(String email) {
        return librarianRepo.existsByEmail(email).isPresent();
    }
}
