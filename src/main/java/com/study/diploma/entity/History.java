package com.study.diploma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "history")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class History extends BaseEntity {
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;


    @Column(name = "reader_id")
    private Long reader;

    @Column(name = "book_id")
    private Long book;

    @Column(name = "rating")
    private Double rating;
}
