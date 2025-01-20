package com.simsimbookstore.apiserver.like.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.like.entity.BookLike;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class BookLikeRepositoryTest {

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    @DisplayName("사용자가 특정 책을 좋아요했는지")
    void findBookLike() {

        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
        entityManager.persist(standardGrade);

        LocalUser testUser = LocalUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test")
                .password("test")
                .build();

        entityManager.persist(testUser);

        Book book = Book.builder().isbn("1231231231111")
                .title("랄라")
                .description("라")
                .bookIndex("qq")
                .publisher("qq")
                .quantity(100)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(100))
                .publicationDate(LocalDate.now())
                .giftPackaging(true)
                .pages(100)
                .bookStatus(BookStatus.ONSALE)
                .giftPackaging(true)
                .viewCount(0L)
                .build();

        entityManager.persist(book);

        BookLike bookLike = BookLike.builder().book(book).user(testUser).build();
        entityManager.persist(bookLike);
        entityManager.flush();

        Optional<BookLike> findBookLike = bookLikeRepository.findBookLike(book.getBookId(), testUser.getUserId());
        Assertions.assertNotNull(findBookLike);
        Assertions.assertEquals(findBookLike.get().getBook().getBookId(), book.getBookId());
        Assertions.assertEquals(findBookLike.get().getUser().getUserId(), testUser.getUserId());
    }

    @Test
    @DisplayName("사용자의 좋아요 개수 반환")
    void getUserLikeNumTest() {
        Book book1 = Book.builder().isbn("1231231231111")
                .title("랄라")
                .description("라")
                .bookIndex("qq")
                .publisher("qq")
                .quantity(100)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(100))
                .publicationDate(LocalDate.now())
                .giftPackaging(true)
                .pages(100)
                .bookStatus(BookStatus.ONSALE)
                .giftPackaging(true)
                .viewCount(0L)
                .build();

        Book book2 = Book.builder().isbn("1231231231112")
                .title("랄라")
                .description("라")
                .bookIndex("qq")
                .publisher("qq")
                .quantity(100)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(100))
                .publicationDate(LocalDate.now())
                .giftPackaging(true)
                .pages(100)
                .bookStatus(BookStatus.ONSALE)
                .giftPackaging(true)
                .viewCount(0L)
                .build();

        entityManager.persist(book1);
        entityManager.persist(book2);

        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
        entityManager.persist(standardGrade);

        LocalUser testUser = LocalUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test")
                .password("test")
                .build();

        entityManager.persist(testUser);

        BookLike bookLike1 = BookLike.builder().book(book1).user(testUser).build();
        BookLike bookLike2 = BookLike.builder().book(book2).user(testUser).build();
        entityManager.persist(bookLike1);
        entityManager.persist(bookLike2);

        entityManager.flush();

        Long userLikeNum = bookLikeRepository.getUserLikeNum(testUser.getUserId());
        Assertions.assertEquals(2, userLikeNum);
    }


}