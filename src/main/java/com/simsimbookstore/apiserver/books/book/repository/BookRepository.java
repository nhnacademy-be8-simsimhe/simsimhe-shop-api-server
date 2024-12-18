package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
