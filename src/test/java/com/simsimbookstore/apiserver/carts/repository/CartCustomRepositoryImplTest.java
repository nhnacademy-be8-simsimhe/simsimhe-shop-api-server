package com.simsimbookstore.apiserver.carts.repository;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.entity.Cart;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.*;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test3")
class CartCustomRepositoryImplTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void init() {
        entityManager.clear();
    }

    @Test
    @DisplayName("장바구니에 있는 도서들 조회")
    void getBookForCart() {
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

        // BookImagePath 추가 (책에 이미지 연결)
        BookImagePath bookImagePath = BookImagePath.builder()
                .book(book)
                .imagePath("test_image_path_")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();
        entityManager.persist(bookImagePath);

        Cart cart = Cart.builder().book(book).user(testUser).build();

        entityManager.persist(cart);


        CartResponseDto responseDto = cartRepository.getBookForCart(book.getBookId());
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(1L, responseDto.getBookId());
        Assertions.assertEquals(100,responseDto.getBookQuantity());


    }


}