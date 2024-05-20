package com.study.diploma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "readers")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reader extends User {
    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone")
    private String phone;

    @Column(name = "place_to_live")
    private String placeToLive;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_reader",
            joinColumns = {@JoinColumn(name = "reader_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")}
    )
    private Set<Book> books = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, phone);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.getReaders().remove(this);
    }
}
