package com.simsimbookstore.apiserver.books.bookcategory.entity;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;


@Builder
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
    private Category catagory;

}
