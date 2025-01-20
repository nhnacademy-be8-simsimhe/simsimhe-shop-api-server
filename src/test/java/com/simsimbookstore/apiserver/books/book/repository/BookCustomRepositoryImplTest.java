package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class BookCustomRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void init() {
        entityManager.clear();
    }


    @Test
    @DisplayName("관리자가 모든 책을 조회")
    void testGetAllBook() {
        // Given - 10개의 도서 추가
        for (int i = 1; i <= 10; i++) {
            Book book = Book.builder()
                    .title("Test Book " + i)
                    .description("Test Description " + i)
                    .bookIndex("Index " + i)
                    .publisher("Test Publisher " + i)
                    .isbn("12345678901" + i) // ISBN 고유값
                    .quantity(10)
                    .price(BigDecimal.valueOf(100 + i))
                    .saleprice(BigDecimal.valueOf(80 + i))
                    .publicationDate(LocalDate.now().minusDays(i)) // 최근 날짜순
                    .pages(200)
                    .bookStatus(BookStatus.ONSALE) // 5번 도서는 삭제 상태
                    .viewCount(0L)
                    .build();
            entityManager.persist(book);

            // BookImagePath 추가 (책에 이미지 연결)
            BookImagePath bookImagePath = BookImagePath.builder()
                    .book(book)
                    .imagePath("test_image_path_" + i)
                    .imageType(BookImagePath.ImageType.THUMBNAIL)
                    .build();
            entityManager.persist(bookImagePath);
        }

        // 데이터를 즉시 반영하여 쿼리에서 조회 가능하도록 함
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // When - 모든 책 조회 (페이징 적용)
        Pageable pageable = PageRequest.of(0, 5); // 한 페이지에 5개씩
        Page<BookListResponse> bookListResponses = bookRepository.getAllBook(pageable);

        // Then - 결과 검증
        assertThat(bookListResponses).isNotNull();
        assertThat(bookListResponses.getContent()).hasSize(5); // 한 페이지당 5권만 조회
        assertThat(bookListResponses.getTotalElements()).isEqualTo(10); // 삭제된 책 제외, 총 9권 조회
        assertThat(bookListResponses.getContent().get(0).getBookStatus()).isNotEqualTo(BookStatus.DELETED); // 삭제된 책이 포함되지 않음
    }
}
