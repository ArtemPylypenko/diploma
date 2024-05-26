package com.study.diploma;

import com.study.diploma.repo.UserRepo;
import com.study.diploma.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@SpringBootApplication
public class LibraryProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryProjectApplication.class, args);
    }
}
