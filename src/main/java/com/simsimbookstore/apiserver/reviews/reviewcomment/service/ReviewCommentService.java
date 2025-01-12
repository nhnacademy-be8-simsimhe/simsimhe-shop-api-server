package com.simsimbookstore.apiserver.reviews.reviewcomment.service;

import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import org.springframework.data.domain.Page;

public interface ReviewCommentService {

    ReviewCommentResponseDTO createReviewComment(ReviewCommentRequestDTO requestDTO, Long reviewId, Long userId);

    ReviewComment updateReviewComment(Long reviewId, Long commentId, ReviewCommentRequestDTO dto);

    ReviewComment getReviewCommentById(Long commentId);

    Page<ReviewCommentResponseDTO> getReviewComments(Long reviewId, int page, int size);

    void deleteReviewComment(Long commentId);

}
