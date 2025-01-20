package com.simsimbookstore.apiserver.users.grade.service.impl;

import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private OrderRepository orderRepository;

    Grade standardGrade;
    Grade royalGrade;
    Grade goldGrade;
    Grade platinumGrade;

    @BeforeEach
    void setUp() {
        standardGrade = Grade.builder()
                .gradeId(1L)
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
        royalGrade = Grade.builder()
                .gradeId(2L)
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(100000))
                .maxAmount(BigDecimal.valueOf(200000))
                .build();
        goldGrade = Grade.builder()
                .gradeId(3L)
                .tier(Tier.GOLD)
                .minAmount(BigDecimal.valueOf(200000))
                .maxAmount(BigDecimal.valueOf(300000))
                .build();
        platinumGrade = Grade.builder()
                .gradeId(4L)
                .tier(Tier.PLATINUM)
                .minAmount(BigDecimal.valueOf(300000))
                .maxAmount(null)
                .build();
    }

    @Test
    @DisplayName("새로운 등급 저장 테스트")
    void save() {
        when(gradeRepository.save(standardGrade)).thenReturn(standardGrade);
        when(gradeRepository.existsByTier(any())).thenReturn(false);
        gradeService.save(standardGrade);

        verify(gradeRepository, times(1)).save(standardGrade);
    }

    @Test
    @DisplayName("새로운 등급 저장 시 이미 등록된 티어")
    void save_AlreadyExistException() {
        when(gradeRepository.existsByTier(any())).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> gradeService.save(standardGrade));
    }

    @Test
    @DisplayName("등급 티어로 조회 테스트")
    void findByTier() {
        when(gradeRepository.findByTier(Tier.STANDARD)).thenReturn(standardGrade);
        gradeService.findByTier(Tier.STANDARD);

        verify(gradeRepository, times(1)).findByTier(Tier.STANDARD);
    }

    @Test
    @DisplayName("등급 아이디로 조회 테스트")
    void findByGradId() {
        when(gradeRepository.findByGradeId(standardGrade.getGradeId())).thenReturn(standardGrade);
        gradeService.findByGradeId(standardGrade.getGradeId());

        verify(gradeRepository, times(1)).findByGradeId(standardGrade.getGradeId());
    }

    @ParameterizedTest
    @CsvSource({
            "99999, STANDARD",
            "100001, ROYAL",
            "200001, GOLD",
            "300001, PLATINUM"
    })
    @DisplayName("금액에 따른 모든 등급 계산")
    void calculateGrade(BigDecimal totalPrice, Tier expectedTier) {
        List<Order> orders = List.of(
                Order.builder().totalPrice(totalPrice).build()
        );

        when(gradeRepository.findAll()).thenReturn(List.of(standardGrade, royalGrade, goldGrade, platinumGrade));
        when(orderRepository.findAllByUserUserId(Mockito.anyLong())).thenReturn(orders);

        Grade grade = gradeService.calculateGrade(1L);
        assertEquals(expectedTier, grade.getTier());

        verify(gradeRepository, times(1)).findAll();
        verify(orderRepository, times(1)).findAllByUserUserId(Mockito.anyLong());
    }

    @Test
    @DisplayName("등급 조회 시 grade 리스트가 없는 경우 에러")
    void calculateGrade_NotFoundException() {
        when(gradeRepository.findAll()).thenReturn(List.of());
        when(orderRepository.findAllByUserUserId(Mockito.anyLong())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> gradeService.calculateGrade(1L));
    }
}