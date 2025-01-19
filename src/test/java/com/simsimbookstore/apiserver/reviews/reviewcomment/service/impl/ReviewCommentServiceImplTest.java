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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.util.List;
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
    private User user2;

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

        user2 = User.builder()
                .userId(2L)
                .userName("Test User2")
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createReviewComment_Success() {
        // Given
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

        // When
        ReviewCommentResponseDTO actualComment = reviewCommentService.createReviewComment(requestDTO, reviewId, userId);

        // Then
        assertThat(actualComment).isNotNull();
        assertThat(actualComment.getContent()).isEqualTo("Test Comment");

        verify(reviewRepository).findById(reviewId);
        verify(userRepository).findById(userId);
        verify(reviewCommentRepository).save(any(ReviewComment.class));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 리뷰 없음")
    void createReviewComment_Fail_NoReview() {
        // Given
        Long reviewId = 1L;
        Long userId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Test Comment");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> reviewCommentService.createReviewComment(requestDTO, reviewId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");

        verify(reviewRepository).findById(reviewId);
        verifyNoInteractions(userRepository, reviewCommentRepository);
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateReviewComment_Success() {
        // Given
        Long reviewId = 1L;
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

        // When
        ReviewComment updatedComment = reviewCommentService.updateReviewComment(reviewId, commentId, requestDTO);

        // Then
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo("Updated Comment");

        verify(reviewCommentRepository).findById(commentId);
        verify(reviewCommentRepository).save(existingComment);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    void updateReviewComment_Fail_NoComment() {

        Long reviewId = 1L;
        Long commentId = 1L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Updated Comment");

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> reviewCommentService.updateReviewComment(reviewId, commentId,requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰 댓글입니다.");

        verify(reviewCommentRepository).findById(commentId);
        verifyNoMoreInteractions(reviewCommentRepository);
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteReviewComment_Success() {
        // Given
        Long commentId = 1L;

        ReviewComment existingComment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .content("Comment to be deleted")
                .review(review)
                .user(user)
                .build();

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // When
        reviewCommentService.deleteReviewComment(commentId);

        // Then
        verify(reviewCommentRepository).findById(commentId);
        verify(reviewCommentRepository).delete(existingComment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteReviewComment_Fail_NoComment() {
        // Given
        Long commentId = 1L;

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> reviewCommentService.deleteReviewComment(commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰 댓글입니다.");

        verify(reviewCommentRepository).findById(commentId);
        verifyNoMoreInteractions(reviewCommentRepository);
    }


    @Test
    @DisplayName("리뷰 댓글 단일 조회 실패 - 해당 댓글이 존재하지 않음")
    void getReviewCommentById_Fail_CommentNotExist(){

        Long commentId = 1L;

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> reviewCommentService.getReviewCommentById(commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰 댓글입니다.");
    }

    @Test
    @DisplayName("리뷰 댓글 단일 조회 성공 테스트")
    void getReviewCommentById_Success(){

        Long commentId = 1L;

        ReviewComment comment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .review(review)
                .user(user)
                .content("리뷰 댓글입니다")
                .created_at(LocalDateTime.now())
                .update_at(LocalDateTime.now())
                .build();

        when(reviewCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ReviewComment callReviewComment = reviewCommentService.getReviewCommentById(commentId);

        assertThat(callReviewComment).isNotNull();
        assertThat(callReviewComment.getContent()).isEqualTo("리뷰 댓글입니다");
        assertThat(callReviewComment.getUser().getUserName() ).isEqualTo("Test User");

        verify(reviewCommentRepository).findById(commentId);
    }

    @Test
    @DisplayName("리뷰 댓글 전체 조회 실패 - 리뷰 없음")
    void getReviewComments_Fail_NoReview(){

        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> reviewCommentService.getReviewComments(reviewId, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
    }


    @Test
    @DisplayName("리뷰 댓글 전체 조회 성공")
    void getReviewComments_Success(){
        Long reviewId = 1L;

        List<ReviewComment> comments = List.of(
                new ReviewComment(1L, "Comment 1", LocalDateTime.now(), LocalDateTime.now(), review, user),
                new ReviewComment(2L, "Comment 2", LocalDateTime.now(), LocalDateTime.now(), review, user2)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewComment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewCommentRepository.findAllByReview(eq(review), eq(pageable))).thenReturn(commentPage);

        // When
        Page<ReviewCommentResponseDTO> result = reviewCommentService.getReviewComments(reviewId, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Comment 1");
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(user.getUserId());
        assertThat(result.getContent().get(1).getContent()).isEqualTo("Comment 2");
        assertThat(result.getContent().get(1).getUserId()).isEqualTo(user2.getUserId());
    }
}