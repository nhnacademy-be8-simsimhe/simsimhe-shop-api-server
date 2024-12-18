package com.simsimbookstore.apiserver.books.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "book_index", nullable = false, columnDefinition = "TEXT")
    private String index;

    @Column(nullable = false, length = 50)
    private String publisher;

    @Column(nullable = false, length = 15, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int quantity = 100;

    // 정가
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // 판매가
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saleprice;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "gift_packaging", nullable = false)
    private boolean giftPackaging = true;

    @Column(nullable = false)
    private int pages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus bookStatus;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
}
