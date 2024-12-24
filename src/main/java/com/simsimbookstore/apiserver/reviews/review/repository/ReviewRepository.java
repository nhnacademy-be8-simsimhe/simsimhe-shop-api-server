package com.simsimbookstore.apiserver.reviews.review.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    int countByBook(Book book);

    Page<Review> findAllByBook(Book book, Pageable pageable);


    Page<Review> findAllByUser(User user, Pageable pageable);



    @Query("SELECT new com.simsimbookstore.apiserver.reviews.dto.ReviewLikeCountDTO(" +
            "r.reviewId, r.title, r.content, r.createdAt, u.userName, COUNT(rl), COUNT(rc)) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "LEFT JOIN ReviewComment rc ON rc.review = r " +
            "GROUP BY r.reviewId, r.title, r.content, r.createdAt, u.userName " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByBookOrderByCreatedAtDesc(Pageable pageable);



    @Query("SELECT new com.simsimbookstore.apiserver.reviews.dto.ReviewLikeCountDTO(" +
            "r.reviewId, r.title, r.content, r.createdAt, u.userName, COUNT(rl), COUNT(rc)) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "LEFT JOIN ReviewComment rc ON rc.review = r " +
            "GROUP BY r.reviewId, r.title, r.content, r.createdAt, u.userName " +
            "ORDER BY COUNT(rl) DESC")
    Page<Review> findAllByBookOrderByLikeDesc(Pageable pageable);



    @Query("SELECT new com.simsimbookstore.apiserver.reviews.dto.ReviewLikeCountDTO(" +
            "r.reviewId, r.title, r.content, r.createdAt, u.userName, COUNT(rl), COUNT(rc)) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "LEFT JOIN ReviewComment rc ON rc.review = r " +
            "GROUP BY r.reviewId, r.title, r.content, r.createdAt, u.userName " +
            "ORDER BY r.score DESC, COUNT(rl) DESC")
    Page<Review> findAllByBookOrderByScoreDesc(Pageable pageable);


}
