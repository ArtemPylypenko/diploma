package com.study.diploma.controllers;

import com.study.diploma.entity.Role;
import com.study.diploma.entity.UserAuth;
import com.study.diploma.services.HistoryService;
import com.study.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.RedirectView;

@EnableWebMvc
@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class LoginController {

    private final HistoryService historyService;
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam("username") String username,
                                               @RequestParam("password") String password) {

        UserAuth user = new UserAuth();
        user.setEmail(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ADMIN);
        userService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Ім'я шаблону для реєстрації
    }

    @GetMapping("/afterLogin")
    public RedirectView afterLogin() {
        historyService.updateReturn();
        Role role = Role.valueOf(getLoggedUserRole());
        return switch (role) {
            case ADMIN -> new RedirectView("/admin");
            case READER -> new RedirectView("/reader");
            case LIBRARIAN -> new RedirectView("/librarian");
        };
    }

    private UserDetails getLoggedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal());
        }
        return null;
    }

    private String getLoggedUserRole() {
        return getLoggedUserDetails().getAuthorities().toArray()[0].toString();
    }
}
