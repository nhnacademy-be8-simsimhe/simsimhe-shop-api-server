package com.simsimbookstore.apiserver.like.repository;

import com.simsimbookstore.apiserver.like.entity.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookLikeRepository extends JpaRepository<BookLike,Long> {

    @Query("select bk from BookLike as bk where bk.book.bookId = :bookId and bk.user.userId = :userId")
    Optional<BookLike> findBookLike(@Param("bookId") Long bookId,@Param("userId") Long userId);

    @Query("select count(*) from BookLike as bk where bk.user.userId = :userId")
    Long getUserLikeNum(@Param("userId") Long userId);


}
