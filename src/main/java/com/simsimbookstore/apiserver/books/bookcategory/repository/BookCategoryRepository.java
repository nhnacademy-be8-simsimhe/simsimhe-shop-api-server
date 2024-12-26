package com.simsimbookstore.apiserver.books.bookcategory.repository;

import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.book.bookId = :bookId")
    void deleteByBookId(Long bookId);
}
