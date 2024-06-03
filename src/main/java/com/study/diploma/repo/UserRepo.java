package com.study.diploma.repo;


import com.study.diploma.entity.UserAuth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<UserAuth, Long> {
    @Query(value = "SELECT * from users where email = ?1", nativeQuery = true)
    Optional<UserAuth> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "delete from users where email = ?1", nativeQuery = true)
    public void deleteUserAuthByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE UserAuth u SET u.email = :newEmail, u.password = :newPassword WHERE u.email = :email")
    void updateByEmail(String email, String newEmail, String newPassword);
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth u SET u.email = :newEmail WHERE u.email = :email")
    void updateByEmail(String email, String newEmail);
}
