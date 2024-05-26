package com.study.diploma.controllers;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.Reader;
import com.study.diploma.entity.Role;
import com.study.diploma.services.BookService;
import com.study.diploma.services.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@EnableWebMvc
@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class LibrarianController {
    private final ReaderService readerService;
    private final BookService bookService;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/librarian")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public String librarianPage(Model model) {

        model.addAttribute("readers", readerService.getAll());
        model.addAttribute("books", bookService.getAll());
        model.addAttribute("booksTop", bookService.getAll().stream()
                .sorted(Comparator.comparingDouble(Book::getRating).reversed())
                .limit(5).toList());

        return "librarian/main_page_librarian";
    }

    @GetMapping("/book/add")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public String getAddBookPage() {
        return "librarian/new_book";
    }

    @PostMapping("/book/add")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView addBook(@RequestParam("name") String name,
                                @RequestParam("authors") String authors,
                                @RequestParam("genres") String genres,
                                @RequestParam("given_by") String givenBy,
                                @RequestParam("isbn") String isbn,
                                @RequestParam("publication") String publication,
                                RedirectAttributes attributes) {
        if (Objects.equals(name, "") || Objects.equals(authors, "") || Objects.equals(givenBy, "") || Objects.equals(isbn, "")) {
            attributes.addFlashAttribute(ERROR, "U should avoid empty fields!");
            return new RedirectView("/librarian");
        }
        Book book = new Book();

        book.setName(name);
        book.setAuthors(authors);
        book.setGenres(Arrays.asList(genres.trim().split("\\s+")));
        book.setGivenBy(givenBy);
        book.setIsbn(isbn);
        book.setPublication(Integer.parseInt(publication));
        book.setRating(3D);
        book.setAvailable(true);

        bookService.save(book);
        return new RedirectView("/librarian");
    }


    @GetMapping("/book/edit/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public String editBook(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.getById(id).get());
        return "librarian/edit_book";
    }

    @PostMapping("/book/edit/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView editBook(@RequestParam("title") String name,
                                 @RequestParam("author") String authors,
                                 @RequestParam("given_by") String givenBy,
                                 @RequestParam("publication") int publication,
                                 @RequestParam("isbn") String isbn,
                                 @PathVariable(value = "id") Long id,
                                 RedirectAttributes attributes) {
        if (Objects.equals(name, "") || Objects.equals(authors, "") || Objects.equals(givenBy, "")
                || Objects.equals(isbn, "")) {
            attributes.addFlashAttribute(ERROR, "U should avoid empty fields!");
            return new RedirectView("/librarian");
        }

        bookService.updateById(name, authors, publication, isbn, givenBy, id);
        attributes.addFlashAttribute(SUCCESS, "Edited successfully");

        return new RedirectView("/librarian");
    }

    @PostMapping("/book/delete/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView deleteBook(@PathVariable("id") Long id, RedirectAttributes attributes) {
        attributes.addFlashAttribute(SUCCESS, "book deleted!");
        bookService.deleteById(id);
        return new RedirectView("/librarian");
    }


    @GetMapping("/reader/add")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public String getAddReaderPage() {
        return "librarian/new_reader";
    }

    @PostMapping("/reader/add")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView addReader(@RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("name") String name,
                                  @RequestParam("surname") String surname,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("place_to_live") String placeToLive,
                                  RedirectAttributes attributes) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty() || placeToLive.isEmpty()) {
            attributes.addFlashAttribute(ERROR, "Avoid empty fields");
            return new RedirectView("/librarian");
        }
        if (readerService.existByEmail(email)) {
            attributes.addFlashAttribute(ERROR, "Reader with such email exist!");
            return new RedirectView("/librarian");
        }

        Reader reader = new Reader();
        reader.setRole(Role.READER);
        reader.setEmail(email);
        reader.setPassword(passwordEncoder.encode(password));
        reader.setName(name);
        reader.setSurname(surname);
        reader.setPhone(phone);
        reader.setPlaceToLive(placeToLive);
        readerService.save(reader);
        attributes.addFlashAttribute(SUCCESS, "Edited successfully");
        return new RedirectView("/librarian");
    }


    @GetMapping("/reader/edit/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public String getEditReader(@PathVariable("id") Long id, Model model) {
        model.addAttribute("reader", readerService.getById(id).get());
        return "librarian/edit_reader";
    }

    @PostMapping("/reader/edit/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView editReader(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   @RequestParam("name") String name,
                                   @RequestParam("surname") String surname,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("place_to_live") String placeToLive,
                                   @PathVariable("id") Long id,
                                   RedirectAttributes attributes) {

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty() || placeToLive.isEmpty()) {
            attributes.addFlashAttribute(ERROR, "Avoid empty fields");
            return new RedirectView("/librarian");
        }
        if (readerService.existByEmail(email) && !readerService.getByEmail(email).get().getPassword().equals(password)) {
            attributes.addFlashAttribute(ERROR, "Reader with such email exist!");
            return new RedirectView("/librarian");
        }
        readerService.update(email, passwordEncoder.encode(password), name, surname, phone, placeToLive, id);
        attributes.addFlashAttribute(SUCCESS, "Edited successfully");

        return new RedirectView("/librarian");
    }


    @PostMapping("/reader/delete/{id}")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    public RedirectView deleteReader(@PathVariable("id") Long id, RedirectAttributes attributes) {
        readerService.deleteById(id);
        attributes.addFlashAttribute(SUCCESS, "Delete successfully");
        return new RedirectView("/librarian");
    }
}
