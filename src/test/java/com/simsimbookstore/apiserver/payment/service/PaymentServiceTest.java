package com.simsimbookstore.apiserver.payment.service;

import com.simsimbookstore.apiserver.payment.exception.PaymentMethodNotFoundException;
import com.simsimbookstore.apiserver.payment.processor.PaymentProcessor;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getProcessor() {
        PaymentProcessor validProcessor = Mockito.mock(PaymentProcessor.class);
        PaymentProcessor anotherProcessor = Mockito.mock(PaymentProcessor.class);

        List<PaymentProcessor> paymentProcessorList = List.of(validProcessor, anotherProcessor);
        paymentService = new PaymentService(paymentProcessorList,paymentMethodRepository, redisTemplate);

        when(validProcessor.checkPayMethod("validValue")).thenReturn(true);

        paymentService.getProcessor("validValue");
        verify(validProcessor, times(1)).checkPayMethod("validValue");
    }

    @Test
    void getProcessor_exception() {
        String paymentMethod = "invalidValue";

        PaymentProcessor invalidProcessor1 = Mockito.mock(PaymentProcessor.class);
        PaymentProcessor invalidProcessor2 = Mockito.mock(PaymentProcessor.class);

        List<PaymentProcessor> paymentProcessorList = List.of(invalidProcessor1, invalidProcessor2);
        paymentService = new PaymentService(paymentProcessorList,paymentMethodRepository, redisTemplate);

        when(invalidProcessor1.checkPayMethod(paymentMethod)).thenReturn(false);
        when(invalidProcessor2.checkPayMethod(paymentMethod)).thenReturn(false);

        Assertions.assertThrows(PaymentMethodNotFoundException.class, () -> paymentService.getProcessor(paymentMethod));
    }
}