package com.simsimbookstore.apiserver.point.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.simsimbookstore.apiserver.point.entity.PointHistory.PointType;
import com.simsimbookstore.apiserver.users.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PointHistoryEntityTest {
    @Test
    void testCreatedAtAndPointDescriptionFields() {
        // 더미 User 객체 생성 (필요한 경우 최소한의 필드만 설정)
        User dummyUser = User.builder()
                .userName("Test User")
                .email("test@example.com")
                .build();

        // 테스트용 데이터 준비
        LocalDateTime now = LocalDateTime.now();
        String description = "Test point description";

        // PointHistory 엔티티 빌더를 통해 인스턴스 생성
        PointHistory pointHistory = PointHistory.builder()
                .pointType(PointType.EARN)
                .amount(100)
                .created_at(now)
                .pointDescription(description)
                .user(dummyUser)
                .build();

        // 설정한 값들이 올바르게 저장되었는지 검증
        assertEquals(now, pointHistory.getCreated_at(), "created_at 필드가 예상한 값과 일치해야 합니다.");
        assertEquals(description, pointHistory.getPointDescription(), "pointDescription 필드가 예상한 값과 일치해야 합니다.");

        // 추가 검증: 다른 필드들도 간단히 확인
        assertEquals(PointType.EARN, pointHistory.getPointType());
        assertEquals(100, pointHistory.getAmount());
        assertEquals(dummyUser, pointHistory.getUser());
    }
}