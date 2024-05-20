package com.study.diploma.services;

import com.study.diploma.entity.User;
import com.study.diploma.entity.UserAuth;
import com.study.diploma.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService implements ClassicalDao<UserAuth> {
    private final UserRepo userRepo;

    @Override
    public UserAuth save(UserAuth user) {
        return userRepo.save(user);
    }

    public UserAuth save(User user) {
        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(user.getEmail());
        userAuth.setPassword(user.getPassword());
        userAuth.setRole(user.getRole());
        return userRepo.save(userAuth);
    }

    @Override
    public void delete(UserAuth user) {
        userRepo.delete(user);
    }

    public void deleteByEmail(String email) {
        userRepo.deleteUserAuthByEmail(email);
    }

    @Override
    public List<UserAuth> getAll() {
        return StreamSupport.stream(userRepo.findAll().spliterator(), false).toList();
    }

    public Optional<UserAuth> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public void updateByOldEmail(String old, String email, String password) {
        userRepo.updateByEmail(old, email, password);
    }
}
