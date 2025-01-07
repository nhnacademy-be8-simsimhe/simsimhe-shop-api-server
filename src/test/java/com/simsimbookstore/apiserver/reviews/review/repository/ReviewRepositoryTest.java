package com.simsimbookstore.apiserver.reviews.review.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.Gender;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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


    }


    @Test
    @DisplayName("데이터 저장 확인")
    void saveData(){

        testBook= bookRepository.save(testBook);

        testUser = userRepository.save(testUser);



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
    void readData(){


    }

    @Test
    void updateData(){

    }

    @Test
    void deleteData(){

    }

}