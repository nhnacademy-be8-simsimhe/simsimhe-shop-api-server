package com.simsimbookstore.apiserver.reviews.reviewcomment.repository;

import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
