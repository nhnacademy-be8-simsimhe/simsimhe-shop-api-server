package com.simsimbookstore.apiserver.books.bookcategory.repository;

import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory,Long> {
}
