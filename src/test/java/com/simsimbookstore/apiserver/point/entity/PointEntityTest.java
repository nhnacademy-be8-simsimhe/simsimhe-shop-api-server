package com.simsimbookstore.apiserver.point.entity;
import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.repository.ReviewPointManageRepository;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test5")
class PointEntityTest {

    @Autowired
    private LocalUserRepository localUserRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private OrderPointManageRepository orderPointManageRepository;
    @Autowired
    private ReviewPointManageRepository reviewPointManageRepository;

    Book book;
    LocalUser testUser;

    @BeforeEach
    void setUp() {
        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
        standardGrade = gradeRepository.save(standardGrade);

        testUser = LocalUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .loginId("test")
                .password("test")
                .build();
        localUserRepository.save(testUser);

        book = Book.builder()
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
        bookRepository.save(book);
    }

    @Test
    void testOrderPointManageMapping() {
        // Order 생성
        Order order = Order.builder()
                .orderEmail("test@example.com")
                .senderName("홍길동")
                .phoneNumber("010-1111-2222")
                .pointEarn(100)
                .orderDate(LocalDateTime.now())
                .deliveryDate(LocalDate.now())
                .pointUse(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .deliveryPrice(BigDecimal.ZERO)
                .originalPrice(BigDecimal.ZERO)
                .orderState(Order.OrderState.PENDING)
                .build();
        orderRepository.save(order);

        // PointHistory 생성
        var pointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(50)
                .created_at(LocalDateTime.now())
                .pointDescription("Test point earning")
                .user(testUser)
                .build();
        pointHistoryRepository.save(pointHistory);

        // OrderPointManage 생성 및 연관관계 설정
        var orderPointManage = OrderPointManage.builder()
                .order(order)
                .pointHistory(pointHistory)
                .build();
        orderPointManageRepository.save(orderPointManage);

        // 저장한 OrderPointManage를 ID로 조회하여 검증
        Optional<OrderPointManage> foundOpt = orderPointManageRepository.findById(orderPointManage.getOrderPointId());
        assertTrue(foundOpt.isPresent(), "OrderPointManage가 존재해야 합니다.");
        var found = foundOpt.get();
        assertNotNull(found.getOrder(), "연관된 Order가 null이 아니어야 합니다.");
        assertNotNull(found.getPointHistory(), "연관된 PointHistory가 null이 아니어야 합니다.");
        assertEquals(50, found.getPointHistory().getAmount(), "PointHistory의 amount가 50이어야 합니다.");
    }

    @Test
    void testReviewPointManageMapping() {
        // Review 생성
        Review review = Review.builder()
                .score(5)
                .title("소년이 온다")
                .content("소년이 온다")
                .createdAt(LocalDateTime.now())
                .book(book)
                .user(testUser)
                .build();
        reviewRepository.save(review);

        // PointHistory 생성
        var pointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(30)
                .created_at(LocalDateTime.now())
                .pointDescription("Review point earning")
                .user(testUser)
                .build();
        pointHistoryRepository.save(pointHistory);

        // ReviewPointManage 생성 및 연관관계 설정
        var reviewPointManage = ReviewPointManage.builder()
                .review(review)
                .pointHistory(pointHistory)
                .build();
        reviewPointManageRepository.save(reviewPointManage);

        // 저장한 ReviewPointManage를 ID로 조회하여 검증
        Optional<ReviewPointManage> foundOpt = reviewPointManageRepository.findById(reviewPointManage.getReviewPointId());
        assertTrue(foundOpt.isPresent(), "ReviewPointManage가 존재해야 합니다.");
        var found = foundOpt.get();
        assertNotNull(found.getReview(), "연관된 Review가 null이 아니어야 합니다.");
        assertNotNull(found.getPointHistory(), "연관된 PointHistory가 null이 아니어야 합니다.");
        assertEquals(30, found.getPointHistory().getAmount(), "PointHistory의 amount가 30이어야 합니다.");
    }
}