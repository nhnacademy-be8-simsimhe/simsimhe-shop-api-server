package com.simsimbookstore.apiserver.reviews.reviewlike.repository;

import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
}
