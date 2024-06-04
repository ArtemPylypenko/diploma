package com.study.diploma.controllers;

import com.study.diploma.entity.Reader;
import com.study.diploma.entity.Role;
import com.study.diploma.services.HistoryService;
import com.study.diploma.services.ReaderService;
import com.study.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@EnableWebMvc
@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class LoginController {

    private final HistoryService historyService;
    private final UserService userService;
    private final ReaderService readerService;

    private static final String ERROR = "error";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("name") String name,
                                     @RequestParam("surname") String surname,
                                     @RequestParam("place_to_live") String placeToLive,
                                     @RequestParam("phone") String phone,
                                     RedirectAttributes attributes) {
        if (userService.findByEmail(email).isPresent()) {
            attributes.addFlashAttribute(ERROR, "Reader with such email already exist, try to change the email");
            return new RedirectView("/register");
        }
        if (password.length() < 8) {
            attributes.addFlashAttribute(ERROR, "Password should be more than 7 symbols");
            return new RedirectView("/register");
        }
        Reader reader = new Reader();
        reader.setEmail(email);
        reader.setPassword(passwordEncoder.encode(password));
        reader.setRole(Role.READER);
        reader.setName(name);
        reader.setSurname(surname);
        reader.setPlaceToLive(placeToLive);
        reader.setPhone(phone);
        readerService.save(reader);
        return new RedirectView("/reader/profile");
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/afterLogin")
    public RedirectView afterLogin() {
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
