package com.simsimbookstore.apiserver.reviews.reviewimage.repository;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImagePathRepository extends JpaRepository<ReviewImagePath, Long> {
    List<ReviewImagePath> findByReview(Review review);
}
