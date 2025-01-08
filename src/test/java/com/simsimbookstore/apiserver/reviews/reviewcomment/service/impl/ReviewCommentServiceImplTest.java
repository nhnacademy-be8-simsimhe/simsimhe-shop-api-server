package com.simsimbookstore.apiserver.reviews.reviewcomment.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import com.simsimbookstore.apiserver.reviews.reviewcomment.repository.ReviewCommentRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewCommentServiceImplTest {
    @InjectMocks
    private ReviewCommentServiceImpl reviewCommentService;

    @Mock
    private ReviewCommentRepository reviewCommentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    private Review review;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 데이터 설정
        review = Review.builder()
                .reviewId(1L)
                .title("Test Review")
                .content("Review Content")
                .createdAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .userId(1L)
                .userName("Test User")
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createReviewComment_Success() {

        Long reviewId = 1L;
        Long userId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Test Comment");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ReviewComment expectedComment = ReviewComment.builder()
                .content(requestDTO.getContent())
                .created_at(LocalDateTime.now())
                .review(review)
                .user(user)
                .build();

        when(reviewCommentRepository.save(any(ReviewComment.class))).thenReturn(expectedComment);


        ReviewCommentResponseDTO actualComment = reviewCommentService.createReviewComment(requestDTO, reviewId, userId);


        assertThat(actualComment).isNotNull();
        assertThat(actualComment.getContent()).isEqualTo("Test Comment");

        verify(reviewRepository).findById(reviewId);
        verify(userRepository).findById(userId);
        verify(reviewCommentRepository).save(any(ReviewComment.class));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 리뷰 없음")
    void createReviewComment_Fail_NoReview() {

        Long reviewId = 1L;
        Long userId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Test Comment");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> reviewCommentService.createReviewComment(requestDTO, reviewId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");

        verify(reviewRepository).findById(reviewId);
        verifyNoInteractions(userRepository, reviewCommentRepository);
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateReviewComment_Success() {

        Long commentId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Updated Comment");

        ReviewComment existingComment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .content("Old Comment")
                .build();

        ReviewComment savedComment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .content("Updated Comment")
                .build();

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(reviewCommentRepository.save(existingComment)).thenReturn(savedComment);


        ReviewComment updatedComment = reviewCommentService.updateReviewComment(requestDTO, commentId);


        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo("Updated Comment");

        verify(reviewCommentRepository).findById(commentId);
        verify(reviewCommentRepository).save(existingComment);    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    void updateReviewComment_Fail_NoComment() {
        // Given
        Long commentId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Updated Comment");

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> reviewCommentService.updateReviewComment(requestDTO, commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰 댓글입니다.");

        verify(reviewCommentRepository).findById(commentId);
        verifyNoMoreInteractions(reviewCommentRepository);
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteReviewComment_Success() {

        Long commentId = 1L;

        ReviewComment existingComment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .content("Comment to be deleted")
                .review(review)
                .user(user)
                .build();

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));


        reviewCommentService.deleteReviewComment(commentId);


        verify(reviewCommentRepository).findById(commentId);
        verify(reviewCommentRepository).delete(existingComment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteReviewComment_Fail_NoComment() {

        Long commentId = 1L;

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> reviewCommentService.deleteReviewComment(commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰 댓글입니다.");

        verify(reviewCommentRepository).findById(commentId);
        verifyNoMoreInteractions(reviewCommentRepository);
    }
}