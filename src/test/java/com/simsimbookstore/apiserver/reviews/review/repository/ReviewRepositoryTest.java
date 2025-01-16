package com.simsimbookstore.apiserver.reviews.review.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GradeRepository gradeRepository;

    User testUser;
    Book testBook;

    @BeforeEach
    void setup(){

        Grade grade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        gradeRepository.save(grade);

        testUser = User.builder()
                .userName("John Doe")
                .mobileNumber("01051278121")
                .email("johndoe@example.com")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .grade(grade)
                .build();


        testBook = Book.builder()
                .title("test book")
                .description("this is test book")
                .bookIndex("Index 1")
                .publisher("Test Publisher 1")
                .isbn("1234567890123")
                .quantity(10)
                .price(new BigDecimal("10000.00"))
                .saleprice(new BigDecimal("8000.00"))
                .publicationDate(LocalDate.now())
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();


        testBook= bookRepository.save(testBook);

        testUser = userRepository.save(testUser);

        saveReview();

    }


    @Test
    @DisplayName("데이터 저장 확인")
    void saveData(){

        Review review = Review.builder()
                .score(5)
                .title("소년이 온다")
                .content("소년이 온다 ")
                .createdAt(LocalDateTime.now())
                .book(testBook)
                .user(testUser)
                .build();


        Review testReview = reviewRepository.save(review);

        assertEquals(5, reviewRepository.findById(testReview.getReviewId()).get().getScore());
    }


    @Test
    @DisplayName("리뷰 수정 테스트")
    void updateData() {

    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void deleteData() {
        Review review = Review.builder()
                .score(5)
                .title("삭제할 리뷰")
                .content("삭제할 내용")
                .createdAt(LocalDateTime.now())
                .book(testBook)
                .user(testUser)
                .build();

        Review savedReview = reviewRepository.save(review);


        reviewRepository.delete(savedReview);

        assertThat(reviewRepository.findById(savedReview.getReviewId())).isEmpty();
    }


    @Test
    @DisplayName("책별 리뷰를 생성일자 순으로 가져오기 테스트")
    void testFindAllByBookOrderByCreatedAtDesc() {
        Long userId = testUser.getUserId();
        Long bookId = testBook.getBookId();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Object[]> result = reviewRepository.findAllByBookOrderByCreatedAtDesc(userId, bookId, pageable);

        log.info("Results: {}", result.getContent());
        log.info("Total Elements: {}", result.getTotalElements());

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isGreaterThan(0); // 기대한 결과 검증
        assertThat(result.getContent().get(0)).isNotNull(); // 첫 번째 데이터 검증
    }



    @Test
    @DisplayName("책별 리뷰를 좋아요 개수 순으로 가져오기 테스트")
    void testFindAllByBookOrderByLikeDesc() {
        Long userId = testUser.getUserId();
        Long bookId = testBook.getBookId();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Object[]> result = reviewRepository.findAllByBookOrderByLikeDesc(userId,bookId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("책별 리뷰를 별점 순으로 가져오기 테스트")
    void testFindAllByBookOrderByScoreDesc() {
        Long userId = testUser.getUserId();
        Long bookId = testBook.getBookId();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Object[]> result = reviewRepository.findAllByBookOrderByScoreDesc(userId,bookId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

//    @Test
//    @DisplayName("유저별 리뷰 가져오기 테스트")
//    void testFindAllByUser() {
//        PageRequest pageable = PageRequest.of(0, 10);
//
//        Page<ReviewLikeCountDTO> result = reviewRepository.findAllByUser(testUser, pageable);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getTotalElements()).isGreaterThan(0);
//    }




    void saveReview(){
        Review testReview1 = Review.builder()
                .score(5)
                .title("테스트 제목")
                .content("테스트 내용")
                .createdAt(LocalDateTime.now())
                .book(testBook)
                .user(testUser)
                .build();

        Review testReview2 = Review.builder()
                .score(4)
                .title("테스트 제목2")
                .content("테스트 내용2")
                .createdAt(LocalDateTime.now())
                .book(testBook)
                .user(testUser)
                .build();

        reviewRepository.save(testReview1);
        reviewRepository.save(testReview2);

    }
}

