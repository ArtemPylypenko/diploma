package com.study.diploma.services;

import com.study.diploma.entity.Reader;
import com.study.diploma.repo.BookReaderRepo;
import com.study.diploma.repo.ReaderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ReaderService implements ClassicalDao<Reader> {
    private final ReaderRepo readerRepo;
    private final UserService userService;
    private final BookReaderRepo bookHistoryRepo;

    @Override
    public Reader save(Reader reader) {
        userService.save(reader);
        return readerRepo.save(reader);
    }

    @Override
    public void delete(Reader reader) {
        String email = reader.getEmail();
        bookHistoryRepo.deleteByReader(reader);
        readerRepo.deleteReaderByEmail(email);
        userService.deleteByEmail(email);
    }

    public void deleteById(Long id) {
        delete(getById(id).get());
    }

    @Override
    public List<Reader> getAll() {
        return StreamSupport.stream(readerRepo.findAll().spliterator(), false).toList();
    }

    public Page<Reader> findAll(PageRequest pageRequest) {
        return readerRepo.findAllReaders(pageRequest);
    }

    public Optional<Reader> findByEmail(String email) {
        return readerRepo.findByEmail(email);
    }

    public Optional<Reader> getById(Long id) {
        return readerRepo.findById(id);
    }

    public Optional<Reader> getByEmail(String email) {
        return readerRepo.findByEmail(email);
    }

    public void update(String email, String password, String name, String surname, String phone, String placeToLive, Long id) {
        String oldEmail = readerRepo.findById(id).get().getEmail();
        userService.updateByOldEmail(oldEmail, email, password);
        readerRepo.update(email, password, name, surname, phone, placeToLive, id);
    }

    public void update(String email, String name, String surname, String phone, String placeToLive, Long id) {
        String oldEmail = readerRepo.findById(id).get().getEmail();
        userService.updateByOldEmail(oldEmail, email);
        readerRepo.update(email, name, surname, phone, placeToLive, id);
    }

    public void updateReader(Long id, String name, String surname, String phone, String email, String password) {
        if (readerRepo.findById(id).isPresent()) {
            Reader existingReader = readerRepo.findById(id).get();
            existingReader.setName(name);
            existingReader.setSurname(surname);
            existingReader.setPhone(phone);
            existingReader.setEmail(email);
            existingReader.setPassword(password);
            readerRepo.save(existingReader);
        }
    }

//    public void updateById(String email, String password, String name, String surname, String phone, String placeToLive, Long id) {
//        String oldEmail = readerRepo.findById(id).get().getEmail();
//        userService.updateByOldEmail(oldEmail, email, password);
//
//        readerRepo.updateReaderEmailAndPasswordById(email, password, id);
//    }

    public boolean existByEmail(String email) {
        return readerRepo.findByEmail(email).isPresent();
    }
}
