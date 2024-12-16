package com.simsimbookstore.apiserver.bookset.bookcategory.domain;

import com.simsimbookstore.apiserver.bookset.book.domain.Book;
import com.simsimbookstore.apiserver.bookset.category.domain.Catagory;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "book_categories")
public class BookCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_category_id")
    private Long bookCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Catagory catagory;

}
