package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {
    boolean existsByIsbn(String isbn13);

    Optional<Book> findByBookIdAndQuantityGreaterThan(Long id, Integer quantity); //재고가 0넘는 책 확인하려고 만들었음

    //도서상태가 삭제가되지 않은 도서만 찾아오기
    @Query("select b from Book as b where b.bookId = :bookId and b.bookStatus != 'DELETED'")
    Optional<Book> findByBookId(@Param("bookId") Long bookId);
}
