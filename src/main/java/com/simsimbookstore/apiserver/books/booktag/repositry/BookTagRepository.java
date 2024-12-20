package com.simsimbookstore.apiserver.books.booktag.repositry;

import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTagRepository extends JpaRepository<BookTag,Long> {

    @Modifying
    @Query("delete from BookTag as bk where bk.book.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
}
