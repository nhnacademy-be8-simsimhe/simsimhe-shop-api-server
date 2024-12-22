package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
    boolean existsByIsbn(String isbn13);
    Optional<Book> findByBookIdAndQuantityGreaterThan(Long id, Integer quantity); //재고가 0넘는 책 확인하려고 만들었음
}
