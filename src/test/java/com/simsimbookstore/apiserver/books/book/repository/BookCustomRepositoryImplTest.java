package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.like.entity.BookLike;
import com.simsimbookstore.apiserver.like.repository.BookLikeRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test4")
class BookCustomRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void init() {
        entityManager.clear();
        entityManager.flush();
    }

    @Test
    @DisplayName("회원이 좋아요한 책을 조회하는 메서드 테스트")
    void testGetUserLikeBook() {

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


        Book book1 = Book.builder()
                .title("Test Book 1")
                .description("Test Description 1")
                .bookIndex("Index 1")
                .publisher("Test Publisher")
                .isbn("12345678901")
                .quantity(10)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(80))
                .publicationDate(LocalDate.now())
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();
        entityManager.persist(book1);

        Book book2 = Book.builder()
                .title("Test Book 2")
                .description("Test Description 2")
                .bookIndex("Index 2")
                .publisher("Test Publisher")
                .isbn("12345678902")
                .quantity(10)
                .price(BigDecimal.valueOf(120))
                .saleprice(BigDecimal.valueOf(90))
                .publicationDate(LocalDate.now())
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();
        entityManager.persist(book2);

        // Given - 책을 좋아요한 기록 추가
        bookLikeRepository.save(BookLike.builder()
                .book(book1)
                .user(testUser)
                .build());

        bookLikeRepository.save(BookLike.builder()
                .book(book2)
                .user(testUser)
                .build());

        // When - 좋아요한 책 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookListResponse> bookListResponses = bookRepository.getUserLikeBook(pageable, testUser.getUserId());

        // Then - 결과 검증
        assertThat(bookListResponses).isNotNull();
    }


    @Test
    @DisplayName("특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 5개 조회")
    void testGetRecommendBook() {
        // Given - 3 카테고리와 각 카테고리 5개의 도서 추가
        Category category1 = Category.builder().categoryName("Fiction").build();
        Category category2 = Category.builder().categoryName("Non-Fiction").build();
        Category category3 = Category.builder().categoryName("Sci-Fi").build();

        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.persist(category3);

        // Create Books
        for (int i = 1; i <= 15; i++) {
            Category category;
            if (i <= 5) category = category1;
            else if (i <= 10) category = category2;
            else category = category3;

            Book book = Book.builder()
                    .title("Test Book " + i)
                    .description("Description " + i)
                    .bookIndex("Index " + i)
                    .publisher("Publisher " + i)
                    .isbn("123412341231" + i)
                    .quantity(10)
                    .price(BigDecimal.valueOf(100 + i))
                    .saleprice(BigDecimal.valueOf(80 + i))
                    .publicationDate(LocalDate.now().minusDays(i))
                    .bookStatus(BookStatus.ONSALE)
                    .viewCount((long) (i * 10))  // Random view count for sorting
                    .build();
            entityManager.persist(book);

            // BookImagePath - Just a placeholder image path
            BookImagePath bookImagePath = BookImagePath.builder()
                    .book(book)
                    .imagePath("test_image_path_" + i)
                    .imageType(BookImagePath.ImageType.THUMBNAIL)
                    .build();
            entityManager.persist(bookImagePath);

            // BookCategory - Link book to category
            BookCategory bookCategory = BookCategory.builder()
                    .book(book)
                    .catagory(category)
                    .build();
            entityManager.persist(bookCategory);
        }

        entityManager.flush();

        // When - Recommend books for category1 excluding the book with ID 1
        List<Long> categoryIds = List.of(category1.getCategoryId(), category2.getCategoryId(), category3.getCategoryId());
        Long excludedBookId = 1L;
        List<BookListResponse> recommendedBooks = bookRepository.getRecommendBook(categoryIds, excludedBookId);

        // Then - 5 books are returned, sorted by view count (descending)
        assertThat(recommendedBooks).hasSize(5); // Check that only 5 books are returned

    }

    @Test
    @DisplayName("주문량이 많은 6개 도서 조회")
    void testGetPopularityBook() {
        // Given - 3개의 도서와 주문 데이터를 준비
        Book book1 = Book.builder()
                .title("Book 1")
                .description("Description 1")
                .bookIndex("Index 1")
                .publisher("Publisher 1")
                .isbn("1234567890123")
                .quantity(10)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(80))
                .publicationDate(LocalDate.now().minusDays(1))
                .pages(200)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        Book book2 = Book.builder()
                .title("Book 2")
                .description("Description 2")
                .bookIndex("Index 2")
                .publisher("Publisher 2")
                .isbn("1234567890124")
                .quantity(5)
                .price(BigDecimal.valueOf(150))
                .saleprice(BigDecimal.valueOf(120))
                .publicationDate(LocalDate.now().minusDays(2))
                .pages(250)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        Book book3 = Book.builder()
                .title("Book 3")
                .description("Description 3")
                .bookIndex("Index 3")
                .publisher("Publisher 3")
                .isbn("1234567890125")
                .quantity(15)
                .price(BigDecimal.valueOf(200))
                .saleprice(BigDecimal.valueOf(180))
                .publicationDate(LocalDate.now().minusDays(3))
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        // BookImagePath 추가 (책에 이미지 연결)
        BookImagePath bookImagePath1 = BookImagePath.builder()
                .book(book1)
                .imagePath("image1.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        BookImagePath bookImagePath2 = BookImagePath.builder()
                .book(book2)
                .imagePath("image2.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        BookImagePath bookImagePath3 = BookImagePath.builder()
                .book(book3)
                .imagePath("image3.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        // 책과 이미지 저장
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.persist(bookImagePath1);
        entityManager.persist(bookImagePath2);
        entityManager.persist(bookImagePath3);

        OrderBook orderBook1 = OrderBook.builder()
                .book(book1)
                .quantity(5)
                .discountPrice(BigDecimal.valueOf(50)) // Set discount_price here
                .salePrice(BigDecimal.valueOf(100))  // Assuming sale price is also required
                .build();

        OrderBook orderBook2 = OrderBook.builder()
                .book(book2)
                .quantity(3)
                .discountPrice(BigDecimal.valueOf(30)) // Set discount_price here
                .salePrice(BigDecimal.valueOf(150))
                .build();

        OrderBook orderBook3 = OrderBook.builder()
                .book(book3)
                .quantity(8)
                .discountPrice(BigDecimal.valueOf(70)) // Set discount_price here
                .salePrice(BigDecimal.valueOf(200))
                .build();

// Persisting the orderBook entities
        entityManager.persist(orderBook1);
        entityManager.persist(orderBook2);
        entityManager.persist(orderBook3);


        entityManager.flush();
        entityManager.clear();

        // When - 인기 도서 조회
        List<BookListResponse> popularityBooks = bookRepository.getPopularityBook();

        // Then - 결과 검증
        assertThat(popularityBooks).isNotEmpty();
        assertThat(popularityBooks).hasSize(3); // 3개 도서 조회
        assertThat(popularityBooks.get(0).getBookId()).isEqualTo(book3.getBookId()); // book3는 가장 많이 주문됨
        assertThat(popularityBooks.get(1).getBookId()).isEqualTo(book1.getBookId()); // book1은 두 번째로 많이 주문됨
        assertThat(popularityBooks.get(2).getBookId()).isEqualTo(book2.getBookId()); // book2는 세 번째로 많이 주문됨
    }

    @Test
    @DisplayName("책 수량을 조회하는 메서드 테스트")
    void testGetBooksForCheck() {
        // Given - 3개의 책 데이터를 준비
        Book book1 = Book.builder()
                .title("Test Book 1")
                .description("Description 1")
                .bookIndex("Index 1")
                .publisher("Publisher 1")
                .isbn("123456789011")
                .quantity(10)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(90))
                .publicationDate(LocalDate.now().minusDays(1))
                .pages(200)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        Book book2 = Book.builder()
                .title("Test Book 2")
                .description("Description 2")
                .bookIndex("Index 2")
                .publisher("Publisher 2")
                .isbn("123456789012")
                .quantity(5)
                .price(BigDecimal.valueOf(200))
                .saleprice(BigDecimal.valueOf(180))
                .publicationDate(LocalDate.now().minusDays(2))
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        Book book3 = Book.builder()
                .title("Test Book 3")
                .description("Description 3")
                .bookIndex("Index 3")
                .publisher("Publisher 3")
                .isbn("123456789013")
                .quantity(15)
                .price(BigDecimal.valueOf(150))
                .saleprice(BigDecimal.valueOf(130))
                .publicationDate(LocalDate.now().minusDays(3))
                .pages(250)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        // 책 이미지 추가
        BookImagePath bookImagePath1 = BookImagePath.builder()
                .book(book1)
                .imagePath("image1.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        BookImagePath bookImagePath2 = BookImagePath.builder()
                .book(book2)
                .imagePath("image2.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        BookImagePath bookImagePath3 = BookImagePath.builder()
                .book(book3)
                .imagePath("image3.jpg")
                .imageType(BookImagePath.ImageType.THUMBNAIL)
                .build();

        // 책과 이미지 저장
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);

        entityManager.persist(bookImagePath1);
        entityManager.persist(bookImagePath2);
        entityManager.persist(bookImagePath3);

        entityManager.flush();

        // When - 책 ID 리스트를 사용하여 책 조회
        List<Long> bookIdList = Arrays.asList(book1.getBookId(), book2.getBookId(), book3.getBookId());
        List<BookListResponse> booksForCheck = bookRepository.getBooksForCheck(bookIdList);
        Assertions.assertEquals(10, booksForCheck.get(0).getQuantity());
        Assertions.assertEquals(5, booksForCheck.get(1).getQuantity());
        Assertions.assertEquals(15, booksForCheck.get(2).getQuantity());


    }


    @Test
    @DisplayName("최근 출판된 도서 8권 조회")
    void testGetNewBookList() {
        // Tag 추가
        Tag tag = Tag.builder()
                .tagName("베스트셀러")
                .isActivated(true)
                .build();
        entityManager.persist(tag);
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
                    .bookStatus(BookStatus.ONSALE) // 정상 상태
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

            // BookTag 추가 (책과 태그 연결)
            BookTag bookTag = BookTag.builder()
                    .book(book)
                    .tag(tag)
                    .build();
            entityManager.persist(bookTag);
        }

        // 데이터를 즉시 반영하여 쿼리에서 조회 가능하도록 함
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // When - 최신 도서 8권 조회
        List<BookListResponse> bookListResponses = bookRepository.getNewBookList();

        // Then - 결과 검증
        assertThat(bookListResponses).isNotNull();


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


    @Test
    @DisplayName("특정 태그에 해당하는 도서 조회")
    void testGetBookListByTag() {
        // Tag 추가
        Tag tag = Tag.builder()
                .tagName("베스트셀러")
                .isActivated(true)
                .build();
        entityManager.persist(tag);

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
                    .bookStatus(BookStatus.ONSALE) // 정상 상태
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

            // BookTag 추가 (책과 태그 연결)
            BookTag bookTag = BookTag.builder()
                    .book(book)
                    .tag(tag)
                    .build();
            entityManager.persist(bookTag);
        }

        // 데이터를 즉시 반영하여 쿼리에서 조회 가능하도록 함
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // When - 특정 태그에 해당하는 도서 조회
        Pageable pageable = PageRequest.of(0, 5); // 한 페이지에 5개씩
        Page<BookListResponse> bookListResponses = bookRepository.getBookListByTag(1L, tag.getTagId(), pageable);

        // Then - 결과 검증
        assertThat(bookListResponses).isNotNull();
        assertThat(bookListResponses.getContent()).hasSize(5); // 한 페이지당 5권만 조회
        assertThat(bookListResponses.getTotalElements()).isEqualTo(10); // 삭제된 책 제외, 총 10권 조회
        assertThat(bookListResponses.getContent().get(0).getBookStatus()).isNotEqualTo(BookStatus.DELETED); // 삭제된 책이 포함되지 않음
    }


}
