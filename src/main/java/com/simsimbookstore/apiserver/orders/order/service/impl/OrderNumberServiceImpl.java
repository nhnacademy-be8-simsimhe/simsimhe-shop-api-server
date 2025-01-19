package com.simsimbookstore.apiserver.orders.order.service.impl;

import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNumberServiceImpl implements OrderNumberService {

    private final StringRedisTemplate redisTemplate;

    // "yyyyMMdd" 날짜 포맷
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 주문번호 생성:
     * 1) "ORDER_NO:yyyyMMdd" 키에 대해 INCR
     * 2) 만약 해당 키가 새로 만들어졌다면 (값이 1이라면) 자정까지 TTL 설정
     * 3) "yyyyMMdd-000001" 형태로 반환
     */
    @Override
    public String generateOrderNo() {
        // NullPointerException 방지: redisTemplate이 null인지 확인
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate is not initialized");
        }

        String today = LocalDate.now().format(DATE_FORMAT);
        String redisKey = "ORDER_NO:" + today;

        Long seq;
        try {
            // 1) INCR 연산 원자적
            seq = redisTemplate.opsForValue().increment(redisKey, 1);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Unable to access Redis operations. Ensure RedisTemplate is configured correctly.", e);
        }

        // seq가 null인 경우 예외 처리
        if (seq == null) {
            throw new IllegalStateException("Failed to increment order number in Redis");
        }

        // 2) seq == 1 이면 "처음 생성된 키"이므로, 자정까지 TTL 설정
        if (seq == 1L) {
            setExpireAtMidnight(redisKey);
        }

        // 3) 주문번호 포맷
        return today + "-" + String.format("%08d", seq);
    }

    /**
     * 자정(익일 0시)에 만료되도록 TTL 설정
     */
    private void setExpireAtMidnight(String key) {
        // 자정 시각 구하기
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();

        // 시스템 TimeZone 기준의 Instant
        Instant midnightInstant = midnight.atZone(ZoneId.systemDefault()).toInstant();

        redisTemplate.expireAt(key, Date.from(midnightInstant));
    }


}
