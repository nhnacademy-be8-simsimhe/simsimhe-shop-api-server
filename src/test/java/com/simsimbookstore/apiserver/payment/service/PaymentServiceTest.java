package com.simsimbookstore.apiserver.payment.service;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.exception.PaymentMethodNotFoundException;
import com.simsimbookstore.apiserver.payment.processor.PaymentProcessor;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private PaymentService paymentService;


    private PaymentProcessor validPaymentProcessor1;
    private PaymentProcessor validPaymentProcessor2;

    @BeforeEach
    void setUp() {
        validPaymentProcessor1 = Mockito.mock(PaymentProcessor.class);
        validPaymentProcessor2 = Mockito.mock(PaymentProcessor.class);

        List<PaymentProcessor> paymentProcessorList = List.of(validPaymentProcessor1, validPaymentProcessor2);
        paymentService = new PaymentService(paymentProcessorList,paymentMethodRepository, redisTemplate);
    }

    @Test
    void getProcessor() {
        when(validPaymentProcessor1.checkPayMethod(any())).thenReturn(true);

        paymentService.getProcessor("validValue");
        verify(validPaymentProcessor1, times(1)).checkPayMethod("validValue");
    }

    @Test
    void getProcessor_exception() {
        String paymentMethod = "invalidValue";

        when(validPaymentProcessor1.checkPayMethod(paymentMethod)).thenReturn(false);
        when(validPaymentProcessor2.checkPayMethod(paymentMethod)).thenReturn(false);

        Assertions.assertThrows(PaymentMethodNotFoundException.class, () -> paymentService.getProcessor(paymentMethod));

        verify(validPaymentProcessor1, times(1)).checkPayMethod(paymentMethod);
        verify(validPaymentProcessor2, times(1)).checkPayMethod(paymentMethod);
    }

    @Test
    void createPaymentRequest() {
        when(validPaymentProcessor1.checkPayMethod(any())).thenReturn(true);

        OrderFacadeResponseDto orderFacadeResponseDto = OrderFacadeResponseDto.builder()
                .method("validMethod")
                .build();
        paymentService.createPaymentRequest(orderFacadeResponseDto);
    };
}