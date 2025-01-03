package com.simsimbookstore.apiserver.reviews.reviewcomment.repository;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    List<ReviewComment> findAllByReview(Review review);
    Page<ReviewComment> findAllByReview(Review review, Pageable pageable);
}
