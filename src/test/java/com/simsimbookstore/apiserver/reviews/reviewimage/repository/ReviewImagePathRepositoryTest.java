package com.simsimbookstore.apiserver.reviews.reviewimage.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
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


@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class ReviewImagePathRepositoryTest {

    @Autowired
    private ReviewImagePathRepository reviewImagePathRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        // 리뷰 데이터 생성
        review1 = Review.builder()
                .title("Great Book")
                .content("This book is fantastic!")
                .createdAt(LocalDateTime.now())
                .build();

        review2 = Review.builder()
                .title("Not Bad")
                .content("This book is okay.")
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // 리뷰 이미지 경로 생성
        ReviewImagePath image1 = ReviewImagePath.builder()
                .imageName("/images/review1/image1.jpg")
                .review(review1)
                .build();

        ReviewImagePath image2 = ReviewImagePath.builder()
                .imageName("/images/review1/image2.jpg")
                .review(review1)
                .build();

        ReviewImagePath image3 = ReviewImagePath.builder()
                .imageName("/images/review2/image1.jpg")
                .review(review2)
                .build();

        reviewImagePathRepository.saveAll(List.of(image1, image2, image3));
    }

    @Test
    @DisplayName("특정 리뷰에 연결된 이미지 경로 조회")
    void findByReview() {
        // 리뷰 1에 연결된 이미지 경로 조회
        List<ReviewImagePath> review1Images = reviewImagePathRepository.findByReview(review1);

        // 검증
        assertThat(review1Images).hasSize(2);
        assertThat(review1Images).extracting("imageName")
                .containsExactlyInAnyOrder("/images/review1/image1.jpg", "/images/review1/image2.jpg");

        // 리뷰 2에 연결된 이미지 경로 조회
        List<ReviewImagePath> review2Images = reviewImagePathRepository.findByReview(review2);

        // 검증
        assertThat(review2Images).hasSize(1);
        assertThat(review2Images).extracting("imageName")
                .containsExactly("/images/review2/image1.jpg");
    }

    @Test
    @DisplayName("전체 이미지 경로 조회")
    void findAll() {
        List<ReviewImagePath> allImages = reviewImagePathRepository.findAll();

        // 검증
        assertThat(allImages).hasSize(3);
        assertThat(allImages).extracting("imageName")
                .containsExactlyInAnyOrder(
                        "/images/review1/image1.jpg",
                        "/images/review1/image2.jpg",
                        "/images/review2/image1.jpg"
                );
    }
}