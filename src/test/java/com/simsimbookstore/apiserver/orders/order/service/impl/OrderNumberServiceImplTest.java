package com.simsimbookstore.apiserver.orders.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class OrderNumberServiceImplTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OrderNumberServiceImpl orderNumberService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGenerateOrderNo_FirstIncrement_SetsExpireAndFormatsNumber() {
        // given
        when(valueOperations.increment(anyString(), eq(1L))).thenReturn(1L);


        // when
        String orderNo = orderNumberService.generateOrderNo();

        // then
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // seq가 1일 때 포맷: yyyyMMdd-00000001
        assertEquals(today + "-00000001", orderNo);

        // expireAt이 설정되었는지 확인
        verify(redisTemplate, times(1)).expireAt(anyString(), any(Date.class));
    }

    @Test
    void testGenerateOrderNo_SubsequentIncrements_NoExpireSet() {
        // given
        when(valueOperations.increment(anyString(), eq(1L))).thenReturn(5L); // 첫 호출이 아닌 경우

        // when
        String orderNo = orderNumberService.generateOrderNo();

        // then
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // seq가 5일 때 포맷: yyyyMMdd-00000005
        assertEquals(today + "-00000005", orderNo);

        // seq가 1이 아니므로 expireAt 설정이 불리면 안 됨
        verify(redisTemplate, never()).expireAt(anyString(), any(Date.class));
    }

    @Test
    void testGenerateOrderNo_RedisTemplateNull_ThrowsException() {
        // given
        OrderNumberServiceImpl service = new OrderNumberServiceImpl(null);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, service::generateOrderNo);
        assertEquals("RedisTemplate is not initialized", exception.getMessage());
    }

    @Test
    void testGenerateOrderNo_IncrementReturnsNull_ThrowsException() {
        // given
        when(valueOperations.increment(anyString(), eq(1L))).thenReturn(null);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> orderNumberService.generateOrderNo());
        assertEquals("Failed to increment order number in Redis", exception.getMessage());
    }

    @Test
    void testGenerateOrderNo_NullPointerDuringIncrement() {
        // given
        // opsForValue() 호출 시 NullPointerException 발생하도록 설정
        when(redisTemplate.opsForValue()).thenThrow(new NullPointerException());

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderNumberService.generateOrderNo());
        assertTrue(exception.getMessage().contains("Unable to access Redis operations"));
        assertNotNull(exception.getCause());
        assertInstanceOf(NullPointerException.class, exception.getCause());
    }
}
