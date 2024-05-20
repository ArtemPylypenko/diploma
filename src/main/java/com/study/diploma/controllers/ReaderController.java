package com.study.diploma.controllers;

import com.study.diploma.dto.HistoryDTO;
import com.study.diploma.entity.Book;
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

    @GetMapping("/reader")
    @PreAuthorize("hasAuthority('READER')")
    public String readerPage(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "reader/book_list_reader";
    }

    @GetMapping("/bookInfo/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public String bookInfo(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getById(id).get();
        model.addAttribute("book", book);
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

    @PostMapping("/bookReturn/{id}")
    @PreAuthorize("hasAuthority('READER')")
    public RedirectView myBooks(@PathVariable("id") Long id, @RequestParam("rating") Double rating) {
        Reader reader = readerService.getById(getLoggedReaderId()).get();
        Book book = bookService.getById(id).get();
        bookService.updateAvailable(true, id);
        //bookService.updateRating(rating, id);

        reader.removeBook(book);
        book.removeReader(reader);
        historyService.updateRating();
        bookReaderService.deteleReaderBook(reader, book);
        historyService.updateRating(rating, reader.getId(), book.getId());

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
