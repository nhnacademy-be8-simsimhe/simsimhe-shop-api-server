package com.simsimbookstore.apiserver.reviews.reviewlike.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class ReviewLikeRepositoryTest {

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private User user1, user2;
    private Review review1, review2;

    @BeforeEach
    void setUp() {
        // 1. Grade 데이터 생성 및 저장
        Grade grade1 = createGrade(Tier.STANDARD, 0.01);
        Grade grade2 = createGrade(Tier.ROYAL, 0.01);

        // 2. User 데이터 생성 및 저장
        user1 = createUser("User1", "user1@example.com", "01011111111", grade1);
        user2 = createUser("User2", "user2@example.com", "01057238902", grade2);

        // 3. Review 데이터 생성 및 저장
        review1 = createReview("Review 1", "Review 1 !!!!!!");
        review2 = createReview("Review 2", "Review 2 !!!!!!");

        // 4. ReviewLike 데이터 생성 및 저장
        createReviewLike(review1, user1);
        createReviewLike(review1, user2);
        createReviewLike(review2, user1);
    }

    @Test
    @DisplayName("특정 사용자가 남긴 모든 좋아요 조회")
    void findAllByUser() {
        // Given
        User savedUser = userRepository.findById(user1.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // When
        List<ReviewLike> likes = reviewLikeRepository.findAllByUser(savedUser);

        // Then
        assertThat(likes).hasSize(2);
    }

    private Grade createGrade(Tier tier, double pointRate) {
        return gradeRepository.save(Grade.builder()
                .tier(tier)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build());
    }

    private User createUser(String name, String email, String mobileNumber, Grade grade) {
        return userRepository.save(User.builder()
                .userName(name)
                .email(email)
                .grade(grade)
                .mobileNumber(mobileNumber)
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Review createReview(String title, String content) {
        return reviewRepository.save(Review.builder()
                .createdAt(LocalDateTime.now())
                .title(title)
                .content(content)
                .build());
    }

    private void createReviewLike(Review review, User user) {
        reviewLikeRepository.save(ReviewLike.builder()
                .created_at(LocalDateTime.now())
                .review(review)
                .user(user)
                .build());
    }


}