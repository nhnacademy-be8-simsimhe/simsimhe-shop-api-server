package com.simsimbookstore.apiserver.orders.delivery.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.exception.DeliveryNotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;


    @Test
    @DisplayName("새로운 Delivery 생성 테스트")
    void createDeliverySuccess() {

        DeliveryRequestDto requestDto = DeliveryRequestDto.builder()
                .deliveryState(Delivery.DeliveryState.READY)
                .deliveryReceiver("홍길동")
                .receiverPhoneNumber("01012345678")
                .postalCode("12345")
                .roadAddress("123시 456동 678구")
                .detailedAddress("아파트")
                .build();

        Delivery delivery = requestDto.toEntity();

        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        Delivery createdDelivery = deliveryService.createDelivery(requestDto);

        assertNotNull(createdDelivery);
        assertEquals("홍길동", createdDelivery.getDeliveryReceiver());
        assertEquals(Delivery.DeliveryState.READY, createdDelivery.getDeliveryState());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    @DisplayName("Delivery ID로 조회 성공 테스트")
    void getDeliveryByIdSuccess() {
        Delivery delivery = Delivery.builder()
                .deliveryId(1L)
                .deliveryReceiver("홍길동")
                .deliveryState(Delivery.DeliveryState.IN_PROGRESS)
                .build();

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        DeliveryResponseDto result = deliveryService.getDeliveryById(1L);


        assertNotNull(result);
        assertEquals(1L, result.getDeliveryId());
        assertEquals("홍길동", result.getDeliveryReceiver());
        verify(deliveryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Delivery ID로 조회 실패 테스트")
    void getDeliveryByIdNotFound() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.empty());

        DeliveryNotFoundException exception = assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.getDeliveryById(1L)
        );

        assertEquals("Delivery not found with ID: 1", exception.getMessage());
        verify(deliveryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Delivery 상태 업데이트 성공 테스트")
    void updateDeliveryStateSuccess() {
        Delivery delivery = Delivery.builder()
                .deliveryId(1L)
                .deliveryState(Delivery.DeliveryState.PENDING)
                .build();

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDto updatedDelivery =
                deliveryService.updateDeliveryState(1L, Delivery.DeliveryState.IN_PROGRESS);


        assertNotNull(updatedDelivery);
        assertEquals(Delivery.DeliveryState.IN_PROGRESS.toString(), updatedDelivery.getDeliveryState());
        verify(deliveryRepository, times(1)).findById(1L);
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    @DisplayName("Delivery 삭제 성공 테스트")
    void deleteDeliverySuccess() {
        when(deliveryRepository.existsById(1L)).thenReturn(true);

        deliveryService.deleteDelivery(1L);

        verify(deliveryRepository, times(1)).existsById(1L);
        verify(deliveryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delivery 삭제 실패 테스트")
    void deleteDeliveryNotFound() {
        when(deliveryRepository.existsById(1L)).thenReturn(false);

        DeliveryNotFoundException exception = assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.deleteDelivery(1L)
        );

        assertEquals("Delivery not found with ID: 1", exception.getMessage());
        verify(deliveryRepository, times(1)).existsById(1L);
        verify(deliveryRepository, never()).deleteById(1L);
    }
}
