package com.simsimbookstore.apiserver.point.repository.custom;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.ReviewPointManage;
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
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import org.springframework.test.context.ActiveProfiles;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test2")
class PointHistoryCustomRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderPointManageRepository orderPointManageRepository;

    @Autowired
    private ReviewPointManageRepository reviewPointManageRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    LocalUser testUser;

    @BeforeEach
    void setUp() {

        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
        standardGrade = gradeRepository.save(standardGrade); // 먼저 저장

        testUser = LocalUser.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade) // 저장된 Grade 참조
                .loginId("test")
                .password("test")
                .build();
        localUserRepository.save(testUser);
    }


    @Test
    @DisplayName("사용자의 포인트 히스토리를 조회하고 페이지네이션을 검증한다.")
    void getPointHistoriesByUserId_ShouldReturnPagedResult() {
        Long userId = 1L;

        Book book = Book.builder()
                .title("Sample Book Title")
                .description("This is a detailed description of the sample book.")
                .bookIndex("Index of the book.")
                .publisher("Sample Publisher")
                .isbn("1234567890123") // 13자리 ISBN
                .quantity(100)
                .price(BigDecimal.valueOf(50000))
                .saleprice(BigDecimal.valueOf(45000))
                .publicationDate(LocalDate.of(2025, 1, 1))
                .giftPackaging(true)
                .pages(300)
                .bookStatus(BookStatus.ONSALE) // Enum 값 설정
                .viewCount(0L)
                .build();

        Book savedBook = bookRepository.save(book);


        PointHistory pointHistory1 = PointHistory.builder()
                .user(testUser)
                .pointType(PointHistory.PointType.EARN)
                .amount(100)
                .created_at(LocalDateTime.of(2025, 1, 2, 10, 0))
                .build();
        PointHistory pointHistory2 = PointHistory.builder()
                .user(testUser)
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(-50)
                .created_at(LocalDateTime.of(2025, 1, 3, 15, 0))
                .build();

        PointHistory pointHistory3 = PointHistory.builder()
                .user(testUser)
                .pointType(PointHistory.PointType.EARN)
                .amount(3000)
                .created_at(LocalDateTime.of(2025, 1, 5, 15, 0))
                .build();

        pointHistoryRepository.save(pointHistory1);
        pointHistoryRepository.save(pointHistory2);
        pointHistoryRepository.save(pointHistory3);

        Delivery delivery = Delivery.builder()
                .deliveryState(Delivery.DeliveryState.PENDING)
                .deliveryReceiver("John Doe")
                .receiverPhoneNumber("010-1234-5678")
                .postalCode("12345")
                .roadAddress("Seoul City Center")
                .detailedAddress("Apartment 101")
                .build();
        delivery = deliveryRepository.save(delivery);

        Order mockOrder = Order.builder()
                .user(testUser)
                .orderName("aaa")
                .orderNumber("aaa")
                .orderDate(LocalDateTime.now())
                .originalPrice(BigDecimal.valueOf(100000))
                .pointUse(BigDecimal.ZERO)
                .totalPrice(BigDecimal.valueOf(100000))
                .deliveryDate(LocalDate.from(LocalDateTime.now()))
                .orderEmail("test@example.com")
                .phoneNumber("010-1234-5678")
                .pointEarn(100)
                .senderName("senderName")
                .deliveryPrice(BigDecimal.valueOf(5000))
                .orderState(Order.OrderState.PENDING)
                .build();
        orderRepository.save(mockOrder);

        Review review = Review.builder()
                .score(5)
                .title("Amazing Book!")
                .content("This book was a fantastic read. Highly recommend it!")
                .createdAt(LocalDateTime.now())
                .book(book)
                .user(testUser)
                .build();
        reviewRepository.save(review);

        OrderPointManage orderPointManage = OrderPointManage.builder()
                .pointHistory(pointHistory1)
                .order(Order.builder().orderId(1L).build())
                .build();
        orderPointManageRepository.save(orderPointManage);

        ReviewPointManage reviewPointManage = ReviewPointManage.builder()
                .pointHistory(pointHistory2)
                .review(review)
                .build();
        reviewPointManageRepository.save(reviewPointManage);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<PointHistoryResponseDto> result = pointHistoryRepository.getPointHistoriesByUserId(userId, pageable);
        assertEquals(1, orderPointManageRepository.count());
        assertEquals(1, reviewPointManageRepository.count());
        assertEquals(3, pointHistoryRepository.count());

        // then
        assertNotNull(result);


        PointHistoryResponseDto first = result.getContent().getFirst();
        assertNotNull(first);
        assertEquals("NONE", first.getSourceType());
        assertEquals(3000, first.getAmount()); // PointHistory3

        PointHistoryResponseDto second = result.getContent().get(1);
        assertNotNull(second);
        assertEquals("REVIEW", second.getSourceType());
        assertEquals(-50, second.getAmount()); // PointHistory2

        PointHistoryResponseDto third = result.getContent().get(2);
        assertNotNull(third);
        assertEquals("ORDER", third.getSourceType());
        assertEquals(100, third.getAmount()); // PointHistory1
    }
}
