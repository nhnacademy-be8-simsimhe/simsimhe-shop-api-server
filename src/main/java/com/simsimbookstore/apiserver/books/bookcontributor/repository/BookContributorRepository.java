package com.simsimbookstore.apiserver.books.bookcontributor.repository;

import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookContributorRepository extends JpaRepository<BookContributor, Long> {

    @Modifying
    @Query("DELETE FROM BookContributor bc WHERE bc.book.bookId = :bookId")
    void deleteByBookId(Long bookId);
}
