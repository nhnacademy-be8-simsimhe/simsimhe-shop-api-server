package com.simsimbookstore.apiserver.orders.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(OrderRepositoryCustomImplTest.QuerydslTestConfig.class)
class OrderRepositoryCustomImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    /**
     * 테스트 환경에서만 사용할 Querydsl 설정.
     */
    @TestConfiguration
    static class QuerydslTestConfig {
        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Test
    void testFindAllOrdersNeedToComplete() {
        // given
        // 1) 31일 지난 주문 (필수 필드 설정)
        Order oldOrder = Order.builder()
                .orderState(Order.OrderState.IN_DELIVERY)
                .deliveryDate(LocalDate.now().minusDays(31))
                // 필수 필드 채우기
                .deliveryPrice(BigDecimal.ZERO)
                .orderDate(LocalDate.now().atStartOfDay())
                .orderEmail("test@example.com")
                .phoneNumber("0000000000")
                .senderName("Test Sender")
                .originalPrice(BigDecimal.TEN)
                .pointUse(BigDecimal.ZERO)
                .totalPrice(BigDecimal.TEN)
                .pointEarn(0)
                .build();
        orderRepository.save(oldOrder);

        // 2) 10일 지난 주문 (필수 필드 설정)
        Order recentOrder = Order.builder()
                .orderState(Order.OrderState.IN_DELIVERY)
                .deliveryDate(LocalDate.now().minusDays(10))
                // 필수 필드 채우기
                .deliveryPrice(BigDecimal.ZERO)
                .orderDate(LocalDate.now().atStartOfDay())
                .orderEmail("test@example.com")
                .phoneNumber("0000000000")
                .senderName("Test Sender")
                .originalPrice(BigDecimal.TEN)
                .pointUse(BigDecimal.ZERO)
                .totalPrice(BigDecimal.TEN)
                .pointEarn(0)
                .build();
        orderRepository.save(recentOrder);

        // DB에 반영 & 영속성 컨텍스트 클리어
        entityManager.flush();
        entityManager.clear();

        // when
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Order> result = orderRepository.findAllOrdersNeedToComplete(thirtyDaysAgo);

        // then
        // 31일 지난 주문만 조회되어야 함
        assertEquals(1, result.size());
        assertEquals(oldOrder.getOrderId(), result.getFirst().getOrderId());
    }

    @Test
    void testFindAllPendingOlderThan() {
        // given
        // 1) '오래된' PENDING 주문: 11분 전
        Order oldPendingOrder = createOrder(
                LocalDateTime.now().minusMinutes(11),
                Order.OrderState.PENDING
        );
        orderRepository.save(oldPendingOrder);

        // 2) '최근' PENDING 주문: 5분 전
        Order recentPendingOrder = createOrder(
                LocalDateTime.now().minusMinutes(5),
                Order.OrderState.PENDING
        );
        orderRepository.save(recentPendingOrder);

        // 3) COMPLETED 주문: 11분 전
        // (조회 결과에 포함되지 않아야 함)
        Order oldCompletedOrder = createOrder(
                LocalDateTime.now().minusMinutes(11),
                Order.OrderState.COMPLETED
        );
        orderRepository.save(oldCompletedOrder);

        // flush & clear: 실제 DB 쿼리 발생, 영속성 컨텍스트 비움
        entityManager.flush();
        entityManager.clear();

        // when
        // 10분 기준
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(10);
        List<Order> result = orderRepository.findAllPendingOlderThan(thresholdTime);

        // then
        // 오래된 PENDING 주문만 1건 조회되어야 함
        assertEquals(1, result.size());
        assertEquals(oldPendingOrder.getOrderId(), result.get(0).getOrderId());
    }

    private Order createOrder(LocalDateTime orderDate, Order.OrderState state) {
        return Order.builder()
                .orderDate(orderDate)
                .orderState(state)
                .deliveryDate(LocalDateTime.now().toLocalDate()) // 예: 오늘 날짜
                .originalPrice(BigDecimal.ZERO)
                .pointUse(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .deliveryPrice(BigDecimal.ZERO)
                .phoneNumber("01000000000")
                .pointEarn(0)
                .senderName("TestSender")
                .orderEmail("test@example.com")
                .build();
    }
}
