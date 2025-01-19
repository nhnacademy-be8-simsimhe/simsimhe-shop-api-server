package com.simsimbookstore.apiserver.reviews.reviewcomment.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import com.simsimbookstore.apiserver.reviews.reviewcomment.repository.ReviewCommentRepository;
import com.simsimbookstore.apiserver.reviews.reviewcomment.service.ReviewCommentService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCommentServiceImpl implements ReviewCommentService {


    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public ReviewCommentResponseDTO createReviewComment(ReviewCommentRequestDTO requestDTO, Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));


        ReviewComment createComment = ReviewComment.builder()
                .content(requestDTO.getContent())
                .created_at(LocalDateTime.now())
                .update_at(LocalDateTime.now())
                .review(review)
                .user(user)
                .build();


        reviewCommentRepository.save(createComment);


        ReviewCommentResponseDTO dto = new ReviewCommentResponseDTO(createComment.getReviewCommentId(), createComment.getContent(), createComment.getCreated_at(), createComment.getUpdate_at(), user.getUserName(), user.getUserId());


        return dto;
    }

    @Override
    public ReviewComment updateReviewComment(Long reviewId, Long commentId, ReviewCommentRequestDTO dto) {
        ReviewComment existingReviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰 댓글입니다."));

        existingReviewComment.setContent(dto.getContent());
        existingReviewComment.setUpdate_at(LocalDateTime.now());

        return reviewCommentRepository.save(existingReviewComment);
    }

    @Override
    public ReviewComment getReviewCommentById(Long commentId) {
        Optional<ReviewComment> reviewCommentOptional = reviewCommentRepository.findById(commentId);

        if (reviewCommentOptional.isPresent())
            return reviewCommentOptional.get();

        throw new NotFoundException("존재하지 않는 리뷰 댓글입니다.");
    }

    @Transactional
    @Override
    public Page<ReviewCommentResponseDTO> getReviewComments(Long reviewId, int page, int size) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        Pageable pageable = PageRequest.of(page, size);

        Page<ReviewComment> reviewComments = reviewCommentRepository.findAllByReview(review, pageable);


        return reviewComments.map(this::convertToDTO);
    }

    @Transactional
    @Override
    public void deleteReviewComment(Long commentId) {
        ReviewComment existingReviewComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰 댓글입니다."));
        reviewCommentRepository.delete(existingReviewComment);
    }

    private ReviewCommentResponseDTO convertToDTO(ReviewComment reviewComment) {
        log.info("dto comment id : {}", reviewComment.getReviewCommentId());
        log.info("dto comment content : {}", reviewComment.getContent());
        log.info("dto comment userId : {}", reviewComment.getUser().getUserId());
        return new ReviewCommentResponseDTO(reviewComment.getReviewCommentId(), reviewComment.getContent(), reviewComment.getCreated_at(), reviewComment.getUpdate_at(), reviewComment.getUser().getUserName(), reviewComment.getUser().getUserId());
    }
}
