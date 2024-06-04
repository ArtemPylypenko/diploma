package com.study.diploma.controllers;

import com.study.diploma.entity.Book;
import com.study.diploma.entity.Comment;
import com.study.diploma.entity.Reader;
import com.study.diploma.services.BookReaderService;
import com.study.diploma.services.BookService;
import com.study.diploma.services.HistoryService;
import com.study.diploma.services.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@EnableWebMvc
@org.springframework.stereotype.Controller
@RequiredArgsConstructor

public class ReaderController {
    private final ReaderService readerService;
    private final BookService bookService;
    private final BookReaderService bookReaderService;
    private final HistoryService historyService;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reader")
    @PreAuthorize("hasAuthority('READER')")
    public String readerPage(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "reader/book_list_reader";
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('READER')")
    public String profile(Model model) {
        model.addAttribute("user", readerService.getById(getLoggedReaderId()).get());
        return "reader/user_profile";
    }

    @PostMapping("/profile/update")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView addBook(@RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("phone") String phone,
                                @RequestParam("email") String email,
                                RedirectAttributes attributes) {
        if (Objects.equals(name, "") || Objects.equals(surname, "") || Objects.equals(phone, "") || Objects.equals(email, "")) {
            attributes.addFlashAttribute(MESSAGE, "U should avoid empty fields!");
            return new RedirectView("/profile");
        }
        readerService.updateReader(getLoggedReaderId(), name, surname, phone, email);
        attributes.addFlashAttribute(SUCCESS, "Changes were applied");

        return new RedirectView("/reader");
    }

    @GetMapping("/bookInfo/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public String bookInfo(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getById(id).get();
        model.addAttribute("book", book);
        List<Comment> comments = new ArrayList<>();

        historyService.getAllByBook(id).forEach(history -> {
            if ((readerService.getById(history.getReader()).isPresent() && history.getComment() != null && !history.getComment().isEmpty())) {
                comments.add(new Comment(readerService.getById(history.getReader()).get().getName(), history.getComment()));
            }
        });
        model.addAttribute("comments", comments);

        return "reader/book_info_reader";
    }

    @PostMapping("/bookReserve/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView takeBook(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Long loggedReaderId = getLoggedReaderId();
        if (bookService.getById(id).get().getAvailable() > 0) {
            if (!bookReaderService.existsByBookAndUser(id, loggedReaderId)) {
                attributes.addFlashAttribute(SUCCESS, "Book was reserved");
                bookReaderService.addReaderBook(readerService.getById(loggedReaderId).get(), bookService.getById(id).get());
                bookService.decreaseAvailable(id);
            } else {
                attributes.addFlashAttribute(ERROR, "You have or reserved this book book!");
            }
        } else {
            attributes.addFlashAttribute(ERROR, "Book is not available!");
        }
        return new RedirectView("/reader");
    }

    @GetMapping("/readerBooks")
    @PreAuthorize("hasAuthority('READER')")
    public String myBooks(Model model) {
        model.addAttribute("books", bookReaderService.getReadersBooks(getLoggedReaderId()));
        return "reader/my_book_reader";
    }

    @GetMapping("/books/search")
    @PreAuthorize("hasAuthority('READER')")
    public String myBooks(@RequestParam("name") String name, Model model) {
        if (bookService.getByName(name) == null || bookService.getByGenres(name) == null) {
            model.addAttribute(MESSAGE, "we can t find books with such name");
            return "reader/book_list_reader";
        }
        Set<Book> result = new HashSet<>(bookService.getByName(name));
        result.addAll(bookService.getByGenres(name));
        result.addAll(bookService.getByAuthors(name));
        if (result.isEmpty()) {
            model.addAttribute(ERROR, "we can t find books with such name");
            return "reader/book_list_reader";
        }
        model.addAttribute("books", result);
        return "reader/book_list_reader";
    }

    @PostMapping("/bookReturn/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView myBooks(@PathVariable("id") Long id, @RequestParam("rating") Double rating, @RequestParam("comment") String comment) {
        Reader reader = readerService.getById(getLoggedReaderId()).get();
        Book book = bookService.getById(id).get();
        bookService.increaseAvailable(id);
        //bookService.updateRating(rating, id);

        reader.removeBook(book);
        book.removeReader(reader);
        bookReaderService.deleteReaderBook(reader, book);
        historyService.updateReturn(rating, reader.getId(), book.getId(), comment);
        bookService.updateRating(historyService.getAVGRating(id), id);
        return new RedirectView("/reader");
    }

    @PostMapping("/cancelReservation/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView cancelReservation(@PathVariable("id") Long bookId) {
        bookReaderService.deleteReaderBook(getLoggedReaderId(), bookId);
        return new RedirectView("/reader");
    }

    @GetMapping("/reader/reservedBooks")
    @PreAuthorize("hasAuthority('READER')")
    public String reservedBooks(Model model) {
        model.addAttribute("books", bookReaderService.getReservedBooks(getLoggedReaderId()));
        return "reader/reserved_books";
    }


    private Long getLoggedReaderId() {
        return readerService.getByEmail(getLoggedUserDetails().getUsername()).get().getId();
    }

    private UserDetails getLoggedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal());
        }
        return null;
    }
}
