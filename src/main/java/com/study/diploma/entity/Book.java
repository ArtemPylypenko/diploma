package com.study.diploma.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity

@Data
@Table(name = "books")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Book extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "authors")
    private String authors;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres;

    @Column(name = "publication")
    private int publication;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "given_by")
    private String givenBy;

    @Column(name = "available")
    private boolean available;

    @Column(name = "rating")
    private Double rating = 3D;

    @ManyToMany(mappedBy = "books", cascade = {CascadeType.DETACH})
    private Set<Reader> readers = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(authors, name, givenBy);
    }

    @Override
    public String toString() {
        return "boooooooooooook";
    }

    public void removeReader(Reader reader) {
        readers.remove(reader);
        reader.getBooks().remove(this);
    }

    public void setRating(Double rating) {
        this.rating = Math.round(rating * 10.0) / 10.0;
    }
}
