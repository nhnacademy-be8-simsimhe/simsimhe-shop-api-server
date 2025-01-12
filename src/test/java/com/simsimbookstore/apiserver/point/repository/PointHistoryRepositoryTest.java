package com.simsimbookstore.apiserver.point.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.localuser.repository.LocalUserRepository;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    LocalUser testUser;

    @BeforeEach
    void setup() {
        // 1) Grade 엔티티 생성
        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.ZERO)
                .maxAmount(BigDecimal.valueOf(999999))
                .build();

        gradeRepository.save(standardGrade);

        // 2) User 엔티티 생성 & persist
        testUser = LocalUser.builder()
                .userName("John Doe1")
                .email("johndoe1@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade) // 저장된 Grade 참조
                .loginId("test1")
                .password("test1")
                .build();
        localUserRepository.save(testUser);

    }

    @Test
    @DisplayName("findByUserUserId - 특정 유저 ID로 PointHistory 목록 조회")
    void testFindByUserUserId() {
        // given
        // User 영속 상태 다시 불러오기 (flush/clear 후)

        // PointHistory 두 건 생성
        PointHistory history1 = PointHistory.builder()
                .amount(100)
                .pointType(PointHistory.PointType.EARN)
                .created_at(LocalDateTime.now())
                .user(testUser)
                .build();

        PointHistory history2 = PointHistory.builder()
                .amount(200)
                .pointType(PointHistory.PointType.EARN)
                .created_at(LocalDateTime.now())
                .user(testUser)
                .build();

        pointHistoryRepository.save(history1);
        pointHistoryRepository.save(history2);

        // when
        List<PointHistory> resultList =
                pointHistoryRepository.findByUserUserId(testUser.getUserId());

        // then
        assertNotNull(resultList, "결과가 null이면 안 됩니다.");
        assertEquals(2, resultList.size(), "2건이 조회되어야 합니다.");
        assertTrue(
                resultList.stream().anyMatch(ph -> ph.getAmount().equals(100)),
                "금액이 100인 이력이 존재해야 합니다."
        );
        assertTrue(
                resultList.stream().anyMatch(ph -> ph.getAmount().equals(200)),
                "금액이 200인 이력이 존재해야 합니다."
        );
    }

    @Test
    @DisplayName("sumAmountByUserId - 특정 유저의 포인트 총합")
    void testSumAmountByUserId() {
        // given

        PointHistory history1 = PointHistory.builder()
                .amount(100)
                .pointType(PointHistory.PointType.EARN)
                .created_at(LocalDateTime.now())
                .user(testUser)
                .build();

        PointHistory history2 = PointHistory.builder()
                .amount(200)
                .pointType(PointHistory.PointType.EARN)
                .created_at(LocalDateTime.now())
                .user(testUser)
                .build();

        pointHistoryRepository.save(history1);
        pointHistoryRepository.save(history2);

        // when
        Integer totalAmount = pointHistoryRepository.sumAmountByUserId(testUser.getUserId()).orElseThrow();

        // then
        assertNotNull(totalAmount, "합계가 null이면 안 됩니다.");
        assertEquals(300, totalAmount, "100 + 200 = 300 이어야 합니다.");
    }
}
