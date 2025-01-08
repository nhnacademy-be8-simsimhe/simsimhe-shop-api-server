package com.simsimbookstore.apiserver.reviews.reviewcomment.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
class ReviewCommentRepositoryTest {
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        // 리뷰 데이터 생성
        review1 = Review.builder()
                .title("Review 1")
                .content("This is the first review")
                .createdAt(LocalDateTime.now())
                .build();

        review2 = Review.builder()
                .title("Review 2")
                .content("This is the second review")
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.saveAll(List.of(review1, review2));


        ReviewComment comment1 = ReviewComment.builder()
                .content("Comment 1 for Review 1")
                .created_at(LocalDateTime.now())
                .review(review1)
                .build();

        ReviewComment comment2 = ReviewComment.builder()
                .content("Comment 2 for Review 1")
                .created_at(LocalDateTime.now())
                .review(review1)
                .build();

        ReviewComment comment3 = ReviewComment.builder()
                .content("Comment 1 for Review 2")
                .created_at(LocalDateTime.now())
                .review(review2)
                .build();

        reviewCommentRepository.saveAll(List.of(comment1, comment2, comment3));
    }

    @Test
    @DisplayName("특정 리뷰에 연결된 모든 댓글 조회")
    void findAllByReview() {
        // 리뷰 1에 연결된 댓글 조회
        List<ReviewComment> commentsForReview1 = reviewCommentRepository.findAllByReview(review1);


        assertThat(commentsForReview1).hasSize(2);
        assertThat(commentsForReview1).extracting("content")
                .containsExactlyInAnyOrder("Comment 1 for Review 1", "Comment 2 for Review 1");

        // 리뷰 2에 연결된 댓글 조회
        List<ReviewComment> commentsForReview2 = reviewCommentRepository.findAllByReview(review2);


        assertThat(commentsForReview2).hasSize(1);
        assertThat(commentsForReview2).extracting("content")
                .containsExactly("Comment 1 for Review 2");
    }

    @Test
    @DisplayName("전체 댓글 조회")
    void findAll() {
        List<ReviewComment> allComments = reviewCommentRepository.findAll();


        assertThat(allComments).hasSize(3);
        assertThat(allComments).extracting("content")
                .containsExactlyInAnyOrder(
                        "Comment 1 for Review 1",
                        "Comment 2 for Review 1",
                        "Comment 1 for Review 2"
                );
    }

    @Test
    @DisplayName("댓글 저장 및 조회")
    void saveAndFindById() {
        ReviewComment newComment = ReviewComment.builder()
                .content("New Comment")
                .created_at(LocalDateTime.now())
                .review(review1)
                .build();

        ReviewComment savedComment = reviewCommentRepository.save(newComment);

        assertThat(savedComment.getReviewCommentId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("New Comment");

        ReviewComment retrievedComment = reviewCommentRepository.findById(savedComment.getReviewCommentId())
                .orElseThrow(() -> new IllegalStateException("Comment not found"));

        assertThat(retrievedComment).isEqualTo(savedComment);
    }
}