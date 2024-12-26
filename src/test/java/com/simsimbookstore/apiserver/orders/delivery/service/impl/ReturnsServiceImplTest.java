package com.simsimbookstore.apiserver.orders.delivery.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.delivery.dto.ReturnsRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.entity.Returns;
import com.simsimbookstore.apiserver.orders.delivery.exception.ReturnsNotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.delivery.repository.ReturnsRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReturnsServiceImplTest {

    @Mock
    private ReturnsRepository returnsRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderBookRepository orderBookRepository;

    @InjectMocks
    private ReturnsServiceImpl returnsService;

    private Returns returns;
    private Delivery delivery;
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        delivery = Delivery.builder()
                .deliveryId(1L)
                .deliveryState(Delivery.DeliveryState.READY)
                .build();

        orderBook = OrderBook.builder()
                .orderBookId(1L)
                .quantity(2)
                .build();

        returns = Returns.builder()
                .returnId(1L)
                .returnReason("Damaged")
                .returnDate(LocalDateTime.now())
                .returnState(Returns.ReturnState.valueOf("RETURN_REQUESTED"))
                .quantity(1)
                .refund(true)
                .damaged(true)
                .delivery(delivery)
                .orderBook(orderBook)
                .build();
    }

    @Test
    @DisplayName("반품 생성 성공")
    void testCreateReturn_Success() {
        ReturnsRequestDto requestDto = ReturnsRequestDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderBookId(orderBook.getOrderBookId())
                .returnReason("Damaged")
                .returnDate(LocalDateTime.now())
                .returnStatus("RETURN_REQUESTED")
                .quantity(1)
                .refund(true)
                .damaged(true)
                .build();

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
        when(returnsRepository.save(any(Returns.class))).thenReturn(returns);

        ReturnsResponseDto responseDto = returnsService.createReturn(requestDto);

        assertNotNull(responseDto);
        assertEquals("Damaged", responseDto.getReturnReason());
        verify(deliveryRepository, times(1)).findById(1L);
        verify(orderBookRepository, times(1)).findById(1L);
        verify(returnsRepository, times(1)).save(any(Returns.class));
    }

    @Test
    @DisplayName("반품 상태 업데이트 성공")
    void testUpdateReturnStatus_Success() {
        when(returnsRepository.findById(1L)).thenReturn(Optional.of(returns));

        ReturnsResponseDto responseDto = returnsService.updateReturnStatus(1L, Returns.ReturnState.RETURN_COMPLETED);

        assertNotNull(responseDto);
        assertEquals("RETURN_COMPLETED", responseDto.getReturnState());
        verify(returnsRepository, times(1)).findById(1L);
        verify(returnsRepository, times(1)).save(returns);
    }

    @Test
    @DisplayName("반품 단일 조회 성공")
    void testGetReturnById_Success() {
        when(returnsRepository.findById(1L)).thenReturn(Optional.of(returns));

        ReturnsResponseDto responseDto = returnsService.getReturnById(1L);

        assertNotNull(responseDto);
        assertEquals("Damaged", responseDto.getReturnReason());
        verify(returnsRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("반품 삭제 성공")
    void testDeleteReturn_Success() {
        when(returnsRepository.findById(1L)).thenReturn(Optional.of(returns));

        returnsService.deleteReturn(1L);

        verify(returnsRepository, times(1)).findById(1L);
        verify(returnsRepository, times(1)).delete(returns);
    }

    @Test
    @DisplayName("반품 생성 실패 - 배송 정보 없음")
    void testCreateReturn_Failure_DeliveryNotFound() {
        ReturnsRequestDto requestDto = ReturnsRequestDto.builder()
                .deliveryId(999L)
                .orderBookId(orderBook.getOrderBookId())
                .returnReason("Damaged")
                .returnDate(LocalDateTime.now())
                .returnStatus("RETURN_REQUESTED")
                .quantity(1)
                .refund(true)
                .damaged(true)
                .build();

        when(deliveryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> returnsService.createReturn(requestDto));
        verify(deliveryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("반품 상태 업데이트 실패 - 반품 요청 없음")
    void testUpdateReturnStatus_Failure_ReturnNotFound() {
        when(returnsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ReturnsNotFoundException.class,
                () -> returnsService.updateReturnStatus(999L, Returns.ReturnState.RETURN_COMPLETED));
        verify(returnsRepository, times(1)).findById(999L);
    }
}
