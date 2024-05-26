package com.study.diploma.controllers;

import com.study.diploma.dto.HistoryDTO;
import com.study.diploma.entity.Book;
import com.study.diploma.entity.Comment;
import com.study.diploma.entity.History;
import com.study.diploma.entity.Reader;
import com.study.diploma.services.BookReaderService;
import com.study.diploma.services.BookService;
import com.study.diploma.services.HistoryService;
import com.study.diploma.services.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                                @RequestParam("oldPassword") String oldPassword,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes attributes) {
        if (Objects.equals(name, "") || Objects.equals(surname, "") || Objects.equals(phone, "") || Objects.equals(email, "")
                || Objects.equals(oldPassword, "")) {
            attributes.addFlashAttribute(MESSAGE, "U should avoid empty fields!");
            return new RedirectView("/profile");
        }
        if (!readerService.getById(getLoggedReaderId()).get().getPassword().equals(oldPassword)) {
            attributes.addFlashAttribute(MESSAGE, "Old password should equal to your current password");
            return new RedirectView("/profile");
        }
        readerService.updateReader(getLoggedReaderId(), name, surname, phone, email, newPassword.isEmpty() ? oldPassword : newPassword);
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

    @PostMapping("/bookTake/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView takeBook(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Long loggedReaderId = getLoggedReaderId();
        if (bookService.getById(id).get().isAvailable()) {
            if (!bookReaderService.existsByBookAndUser(id, loggedReaderId)) {
                attributes.addFlashAttribute(SUCCESS, "Book added to your books");
                bookReaderService.addReaderBook(readerService.getById(loggedReaderId).get(), bookService.getById(id).get());
                bookService.updateAvailable(false, id);
            } else {
                attributes.addFlashAttribute(ERROR, "Reader has such book!");
            }
        } else {
            attributes.addFlashAttribute(ERROR, "Book is not available!");
        }
        return new RedirectView("/reader");
    }

    @GetMapping("/readerBooks")
    @PreAuthorize("hasAuthority('READER')")
    public String myBooks(Model model) {
        model.addAttribute("books", readerService.getById(getLoggedReaderId()).get().getBooks().stream().filter(book ->
                !book.isAvailable()));
        return "reader/my_book_reader";
    }

    @GetMapping("/books/search")
    @PreAuthorize("hasAuthority('READER')")
    public String myBooks(@RequestParam("name") String name, Model model) {
        if (bookService.getByName(name) == null || bookService.getByGenres(name) == null) {
            model.addAttribute(MESSAGE, "we can t find books with such name");
            return "reader/book_list_reader";
        }
        List<Book> result = bookService.getByName(name);
        result.addAll(bookService.getByGenres(name));
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
        bookService.updateAvailable(true, id);
        //bookService.updateRating(rating, id);

        reader.removeBook(book);
        book.removeReader(reader);
        historyService.updateReturn();
        bookReaderService.deteleReaderBook(reader, book);
        historyService.updateReturn(rating, reader.getId(), book.getId(), comment);
        bookService.updateRating(historyService.getAVGRating(id), id);
        return new RedirectView("/reader");
    }

    @GetMapping("reader/history")
    @PreAuthorize("hasAuthority('READER')")
    public String myHistory(Model model) {
        List<HistoryDTO> dtos = new ArrayList<>();
        historyService.getAllByReader(getLoggedReaderId()).stream().filter(history ->
                        history.getReturnedAt() != null && history.getCreatedAt() != null)
                .forEach(h -> dtos.add(historyService.historyToDto(h)));
        model.addAttribute("history", dtos);
        return "reader/book_history_reader";
    }

    @GetMapping("reader/historySorted")
    @PreAuthorize("hasAuthority('READER')")
    public String myHistorySorted(Model model, @RequestParam("dateTimeBefore") LocalDateTime before, @RequestParam("dateTimeAfter") LocalDateTime after) {
        List<HistoryDTO> dtos = new ArrayList<>();
        historyService.getAllByReader(getLoggedReaderId()).stream().filter(history ->
                        history.getReturnedAt() != null && history.getCreatedAt() != null)
                .filter(h2 ->
                        h2.getCreatedAt().isAfter(after) && h2.getReturnedAt().isBefore(before))
                .forEach(h -> dtos.add(historyService.historyToDto(h)));
        model.addAttribute("history", dtos);
        return "reader/book_history_reader";
    }

    @GetMapping("reader/historySortUp")
    @PreAuthorize("hasAuthority('READER')")
    public String myHistorySortUp(Model model) {
        List<History> allByReader = historyService.getAllByReader(getLoggedReaderId()).stream().filter(history ->
                history.getReturnedAt() != null && history.getCreatedAt() != null).toList();
        List<HistoryDTO> dtos = new ArrayList<>();
        allByReader.forEach(h -> dtos.add(historyService.historyToDto(h)));

        List<HistoryDTO> sorted = dtos.stream().sorted(Comparator.comparing(HistoryDTO::getCreatedAt)).toList();

        model.addAttribute("history", sorted);
        return "reader/book_history_reader";
    }

    @GetMapping("reader/historySortDown")
    @PreAuthorize("hasAuthority('READER')")
    public String myHistorySortDown(Model model) {
        List<History> allByReader = historyService.getAllByReader(getLoggedReaderId()).stream().filter(history ->
                history.getReturnedAt() != null && history.getCreatedAt() != null).toList();
        List<HistoryDTO> dtos = new ArrayList<>();
        allByReader.forEach(h -> dtos.add(historyService.historyToDto(h)));

        List<HistoryDTO> sorted = dtos.stream().sorted(Comparator.comparing(HistoryDTO::getCreatedAt).reversed()).toList();

        model.addAttribute("history", sorted);
        return "reader/book_history_reader";
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
