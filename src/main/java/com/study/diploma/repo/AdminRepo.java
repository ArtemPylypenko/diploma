package com.study.diploma.repo;

import com.study.diploma.entity.Admin;
import com.study.diploma.entity.UserAuth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepo extends CrudRepository<Admin, Long> {
    @Query(value = "select * from admins where email = ?1", nativeQuery = true)
    Optional<UserAuth> findByEmail(String email);

}
