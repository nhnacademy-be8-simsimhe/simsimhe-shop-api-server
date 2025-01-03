package com.simsimbookstore.apiserver.reviews.review.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    int countByBook(Book book);

    Page<Review> findAllByBook(Book book, Pageable pageable);


    Page<ReviewLikeCountDTO> findAllByUser(User user, Pageable pageable);



    @Query(value = "SELECT r.review_id AS reviewId, r.title AS title, r.content AS content, r.created_at AS createdAt, " +
            "u.user_name AS userName, r.score AS score, COUNT(rl.review_like_id) AS likeCount, " +
            "COUNT(rc.review_comment_id) AS commentCount, GROUP_CONCAT(ri.image_name) AS imagePaths " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "LEFT JOIN review_likes rl ON rl.review_id = r.review_id " +
            "LEFT JOIN review_comments rc ON rc.review_id = r.review_id " +
            "LEFT JOIN review_image_paths ri ON ri.review_id = r.review_id " +
            "WHERE r.book_id = :bookId " +
            "GROUP BY r.review_id, r.title, r.content, r.created_at, u.user_name " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
    Page<Object[]> findAllByBookOrderByCreatedAtDesc(Long bookId, Pageable pageable);



    @Query("SELECT new com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO(" +
            "r.reviewId, r.title, r.content, r.createdAt, u.userName, r.score, COUNT(rl), COUNT(rc)) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "LEFT JOIN ReviewComment rc ON rc.review = r " +
            "LEFT JOIN ReviewImagePath ri ON ri.review = r " +
            "WHERE r.book.bookId = :bookId "+
            "GROUP BY r.reviewId, r.title, r.content, r.createdAt, u.userName " +
            "ORDER BY COUNT(rl) DESC")
    Page<ReviewLikeCountDTO> findAllByBookOrderByLikeDesc(Long bookId, Pageable pageable);



    @Query("SELECT new com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO(" +
            "r.reviewId, r.title, r.content, r.createdAt, u.userName, r.score, COUNT(rl), COUNT(rc)) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "LEFT JOIN ReviewComment rc ON rc.review = r " +
            "LEFT JOIN ReviewImagePath ri ON ri.review = r " +
            "WHERE r.book.bookId = :bookId "+
            "GROUP BY r.reviewId, r.title, r.content, r.createdAt, u.userName " +
            "ORDER BY r.score DESC, COUNT(rl) DESC")
    Page<ReviewLikeCountDTO> findAllByBookOrderByScoreDesc(Long bookId, Pageable pageable);


}
