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
    GradeRepository gradeRepository;
    @Autowired
    private ReviewRepository reviewRepository;


    @BeforeEach
    void setUp() {

        Grade grade1 = createGrade(Tier.STANDARD);
        Grade grade2 = createGrade(Tier.ROYAL);

        gradeRepository.save(grade1);
        gradeRepository.save(grade2);


        User user1 = createUser("User1", "user1@example.com", "01011111111", grade1);
        User user2 =  createUser("User2", "user2@example.com", "01057238902", grade2);

        userRepository.save(user1);
        userRepository.save(user2);



        Review review1 = Review.builder()
                .createdAt(LocalDateTime.now())
                .title("Review 1")
                .content("Review 1 !!!!!!")
                .build();


        Review review2 = Review.builder()
                .createdAt(LocalDateTime.now())
                .title("Review 2")
                .content("Review 2 !!!!!!")
                .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        ReviewLike like1 = ReviewLike.builder().created_at(LocalDateTime.now()).review(review1).user(user1).build();
        ReviewLike like2 = ReviewLike.builder().created_at(LocalDateTime.now()).review(review1).user(user2).build();
        ReviewLike like3 = ReviewLike.builder().created_at(LocalDateTime.now()).review(review2).user(user1).build();

        reviewLikeRepository.saveAll(List.of(like1, like2, like3));
    }

    @Test
    @DisplayName("특정 사용자의 좋아요 수 조회")
    void countByUser() {
        User user = User.builder().userId(1L).build();

        long count = reviewLikeRepository.countByUser(user);

        assertThat(count).isEqualTo(2);
    }


    @Test
    @DisplayName("특정 리뷰에 대한 좋아요 수 조회")
    void countByReview() {
        Review review = Review.builder().reviewId(2L).build();

        long count = reviewLikeRepository.countByReview(review);

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 사용자가 특정 리뷰를 좋아요 했는지 확인")
    void findByUserAndReview() {
        User user = User.builder().userId(1L).build();

        Review review = Review.builder().reviewId(1L).build();

        Optional<ReviewLike> like = reviewLikeRepository.findByUserAndReview(user, review);

        assertThat(like).isPresent();
    }

    @Test
    @DisplayName("특정 리뷰에 대한 모든 좋아요 조회")
    void findAllByReview() {
        Review review = Review.builder().reviewId(1L).build();

        List<ReviewLike> likes = reviewLikeRepository.findAllByReview(review);

        assertThat(likes).hasSize(2);
    }

    @Test
    @DisplayName("특정 사용자가 남긴 모든 좋아요 조회")
    void findAllByUser() {
        User user = User.builder().userId(1L).build();

        List<ReviewLike> likes = reviewLikeRepository.findAllByUser(user);

        assertThat(likes).hasSize(2);
    }


    private Grade createGrade(Tier tier) {
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


}