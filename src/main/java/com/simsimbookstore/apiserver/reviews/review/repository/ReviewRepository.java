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


    Page<Review> findAllByBook(Book book, Pageable pageable);


    Page<ReviewLikeCountDTO> findAllByUser(User user, Pageable pageable);



    @Query(value = "SELECT " +
            "r.review_id AS reviewId, " +
            "r.title AS title, " +
            "r.content AS content, " +
            "r.created_at AS createdAt, " +
            "u.user_name AS userName, " +
            "u.user_id AS userId, " +
            "r.score AS score, " +
            "COUNT(DISTINCT rl.review_like_id) AS likeCount, " +
            "COUNT(DISTINCT rc.review_comment_id) AS commentCount, " +
            "GROUP_CONCAT(ri.image_name) AS imagePaths, " +
            "CASE WHEN rl_specific.user_id IS NOT NULL THEN true ELSE false END AS likedByUser " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "LEFT JOIN review_likes rl ON rl.review_id = r.review_id " +
            "LEFT JOIN review_comments rc ON rc.review_id = r.review_id " +
            "LEFT JOIN review_image_paths ri ON ri.review_id = r.review_id " +
            "LEFT JOIN review_likes rl_specific ON rl_specific.review_id = r.review_id AND rl_specific.user_id = :userId " +
            "WHERE r.book_id = :bookId " +
            "GROUP BY " +
            "r.review_id, r.title, r.content, r.created_at, u.user_name, u.user_id, r.score " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
    Page<Object[]> findAllByBookOrderByCreatedAtDesc(Long userId, Long bookId, Pageable pageable);


    //querydsl 구성하기

    @Query(value = "SELECT " +
            "r.review_id AS reviewId, " +
            "r.title AS title, " +
            "r.content AS content, " +
            "r.created_at AS createdAt, " +
            "u.user_name AS userName, " +
            "u.user_id AS userId, " +
            "r.score AS score, " +
            "COUNT(DISTINCT rl.review_like_id) AS likeCount, " +
            "COUNT(DISTINCT rc.review_comment_id) AS commentCount, " +
            "GROUP_CONCAT(ri.image_name) AS imagePaths, " +
            "CASE WHEN rl_specific.user_id IS NOT NULL THEN true ELSE false END AS likedByUser " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "LEFT JOIN review_likes rl ON rl.review_id = r.review_id " +
            "LEFT JOIN review_comments rc ON rc.review_id = r.review_id " +
            "LEFT JOIN review_image_paths ri ON ri.review_id = r.review_id " +
            "LEFT JOIN review_likes rl_specific ON rl_specific.review_id = r.review_id AND rl_specific.user_id = :userId " +
            "WHERE r.book_id = :bookId " +
            "GROUP BY " +
            "r.review_id, r.title, r.content, r.created_at, u.user_name, u.user_id, r.score " +
            "ORDER BY COUNT(rl.review_like_id) DESC",
            nativeQuery = true)
    Page<Object[]> findAllByBookOrderByLikeDesc(Long userId, Long bookId, Pageable pageable);




    @Query(value = "SELECT " +
            "r.review_id AS reviewId, " +
            "r.title AS title, " +
            "r.content AS content, " +
            "r.created_at AS createdAt, " +
            "u.user_name AS userName, " +
            "u.user_id AS userId, " +
            "r.score AS score, " +
            "COUNT(DISTINCT rl.review_like_id) AS likeCount, " +
            "COUNT(DISTINCT rc.review_comment_id) AS commentCount, " +
            "GROUP_CONCAT(ri.image_name) AS imagePaths, " +
            "CASE WHEN rl_specific.user_id IS NOT NULL THEN true ELSE false END AS likedByUser " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "LEFT JOIN review_likes rl ON rl.review_id = r.review_id " +
            "LEFT JOIN review_comments rc ON rc.review_id = r.review_id " +
            "LEFT JOIN review_image_paths ri ON ri.review_id = r.review_id " +
            "LEFT JOIN review_likes rl_specific ON rl_specific.review_id = r.review_id AND rl_specific.user_id = :userId " +
            "WHERE r.book_id = :bookId " +
            "GROUP BY " +
            "r.review_id, r.title, r.content, r.created_at, u.user_name, u.user_id, r.score " +
            "ORDER BY r.score DESC, COUNT(rl.review_like_id) DESC",
            nativeQuery = true)
    Page<Object[]> findAllByBookOrderByScoreDesc(Long userId, Long bookId, Pageable pageable);



    @Query(value = "select count(o.order_id) " +
            "from orders as o " +
            "LEFT JOIN order_books as ob ON o.order_id = ob.order_id\n" +
            "LEFT JOIN reviews AS r ON ob.book_id = r.book_id AND o.user_id = r.user_id\n" +
            "WHERE o.user_id=:userId AND ob.book_id =:bookId",
    nativeQuery = true)
    long bookOrderCheck(Long userId, Long bookId);

    @Query(value = "select count(review_id) > 0 from reviews where user_id = :userId AND book_id = :bookId",
            nativeQuery = true)
    long alreadyExistCheck(Long userId, Long bookId);


    @Query(value = "SELECT " +
            "    r.review_id AS reviewId, " +
            "    b.book_id AS bookId, " +
            "    b.title AS bookTitle, " +
            "    c.contributor_name AS contributor, " +
            "    bi.image_path AS bookImagePath, " +
            "    r.title AS title, " +
            "    r.content AS content, " +
            "    r.created_at AS createdAt, " +
            "    u.user_name AS userName, " +
            "    u.user_id AS userId, " +
            "    r.score AS score, " +
            "    COUNT(DISTINCT rl.review_like_id) AS likeCount, " +
            "    COUNT(DISTINCT rc.review_comment_id) AS commentCount, " +
            "    GROUP_CONCAT(ri.image_name) AS imagePaths " +
            "FROM reviews r " +
            "         INNER JOIN books b ON r.book_id = b.book_id " +
            "         INNER JOIN book_contributors bc ON bc.book_id = b.book_id " +
            "         INNER JOIN contributors c ON bc.contributor_id = c.contributor_id AND c.contributor_role = '지은이' " +
            "         LEFT JOIN  book_image_paths bi ON bi.book_id = b.book_id " +
            "         LEFT JOIN users u ON r.user_id = u.user_id " +
            "         LEFT JOIN review_likes rl ON rl.review_id = r.review_id " +
            "         LEFT JOIN review_comments rc ON rc.review_id = r.review_id " +
            "         LEFT JOIN review_image_paths ri ON ri.review_id = r.review_id " +
            "         LEFT JOIN review_likes rl_specific ON rl_specific.review_id = r.review_id AND rl_specific.user_id = :userId " +
            "WHERE r.user_id = :userId " +
            "GROUP BY " +
            "    r.review_id, b.book_id, b.title, c.contributor_name, bi.image_path, r.title, r.content, r.created_at, u.user_name, u.user_id, r.score " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true
    )
    Page<Object[]> getUserReviews(Long userId, Pageable pageable);


    @Query(value = "SELECT " +
            "    b.book_id AS bookId, " +
            "    b.title AS bookTitle, " +
            "    c.contributor_name AS contributor, " +
            "    bi.image_path AS bookImage, " +
            "    MAX(o.order_date) AS orderDate " +
            "FROM orders o " +
            "         INNER JOIN order_books ob ON o.order_id = ob.order_id " +
            "         INNER JOIN books b ON ob.book_id = b.book_id " +
            "         INNER JOIN book_contributors bc ON bc.book_id = b.book_id " +
            "         INNER JOIN contributors c ON c.contributor_id = bc.contributor_id AND c.contributor_role = '지은이' " +
            "         LEFT JOIN book_image_paths bi ON bi.book_id = b.book_id " +
            "         LEFT JOIN reviews r ON r.book_id = b.book_id AND r.user_id = o.user_id " +
            "WHERE o.user_id = :userId " +
            "  AND r.review_id IS NULL " +
            "GROUP BY " +
            "    b.book_id, " +
            "    b.title, " +
            "    c.contributor_name, " +
            "    bi.image_path " +
            "ORDER BY MAX(o.order_date) DESC",
            nativeQuery = true
    )
    Page<Object[]> getEligibleBooksForReview(Long userId, Pageable pageable);



}
