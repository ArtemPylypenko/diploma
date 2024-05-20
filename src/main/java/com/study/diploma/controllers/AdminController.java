package com.study.diploma.controllers;

import com.study.diploma.entity.Librarian;
import com.study.diploma.entity.Role;
import com.study.diploma.services.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@EnableWebMvc
@org.springframework.stereotype.Controller
@RequiredArgsConstructor

public class AdminController {
    private final LibrarianService librarianService;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminPage(Model model) {
        model.addAttribute("librarians", librarianService.getAll());
        return "admin/main_page_admin";
    }

    @GetMapping("/librarian/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAddLibrarianPAge() {
        return "admin/new_librarian";
    }

    @PostMapping("/librarian/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView addLibrarian(@RequestParam("email") String email, @RequestParam String password, RedirectAttributes attributes) {
        if (Objects.equals(email, "") || Objects.equals(password, "")) {
            attributes.addFlashAttribute(ERROR, "U should avoid empty fields!");
            return new RedirectView("/admin");
        }
        if (!librarianService.existByEmail(email)) {
            Librarian librarian = new Librarian();
            librarian.setRole(Role.LIBRARIAN);
            librarian.setPassword(password);
            librarian.setEmail(email);
            librarianService.save(librarian);
            attributes.addFlashAttribute(SUCCESS, "Librarian added!");
        } else {
            attributes.addFlashAttribute(ERROR, "Librarian with such email already exist!");
        }
        return new RedirectView("/admin");
    }

    @GetMapping("/librarian/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getEditLibrarianPage(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("librarian", librarianService.getById(id).get());
        return "admin/edit_librarian";
    }

    @PostMapping("/librarian/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView editLibrarian(@RequestParam("email") String email, @RequestParam("password") String password, @PathVariable(value = "id") Long id, RedirectAttributes attributes) {
        if (Objects.equals(email, "") || Objects.equals(password, "")) {
            attributes.addFlashAttribute(ERROR, "U should avoid empty fields!");
            return new RedirectView("/admin");
        }

        if (librarianService.existByEmail(email)) {
            if (email.equals(librarianService.getById(id).get().getEmail())) {
                librarianService.updateById(email, password, id);
                attributes.addFlashAttribute(SUCCESS, "Edited successfully");
            } else {
                attributes.addFlashAttribute(ERROR, "Such email already exist!");
            }
            return new RedirectView("/admin");
        }
        librarianService.updateById(email, password, id);
        attributes.addFlashAttribute(SUCCESS, "Edited successfully");

        return new RedirectView("/admin");
    }

    @PostMapping("/librarian/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView editLibrarian(@PathVariable(value = "id") Long id, RedirectAttributes attributes) {

        librarianService.deleteById(id);
        attributes.addFlashAttribute(SUCCESS, "Deleted successfully");

        return new RedirectView("/admin");
    }
}
