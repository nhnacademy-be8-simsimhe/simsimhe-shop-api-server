package com.simsimbookstore.apiserver.users.grade.repository;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    private Grade testGrade;
    private Grade testGrade1;
    private Grade testGrade2;

    @BeforeEach
    void setUp() {
        testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        testGrade1 = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        testGrade2 = Grade.builder()
                .tier(Tier.PLATINUM)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        gradeRepository.save(testGrade);
        gradeRepository.save(testGrade1);
        gradeRepository.save(testGrade2);
    }

    @Test
    void findByGradeTier() {
        Grade grade = gradeRepository.findByTier(testGrade.getTier());
        assertNotNull(grade);
        assertEquals(testGrade.getGradeId(), grade.getGradeId());
        assertEquals(testGrade.getTier(), grade.getTier());
        assertEquals(testGrade.getMinAmount(), grade.getMinAmount());

        assertNull(gradeRepository.findByTier(Tier.GOLD));
    }

    @Test
    void findByGradeId() {
        Grade grade = gradeRepository.findByGradeId(testGrade.getGradeId());
        assertNotNull(grade);
        assertEquals(testGrade.getGradeId(), grade.getGradeId());
        assertEquals(testGrade.getTier(), grade.getTier());
        assertEquals(testGrade.getMinAmount(), grade.getMinAmount());
        assertEquals(testGrade.getMaxAmount(), grade.getMaxAmount());
    }

    @Test
    void existsByTier() {
        assertTrue(gradeRepository.existsByTier(testGrade.getTier()));
        assertTrue(gradeRepository.existsByTier(testGrade1.getTier()));
        assertTrue(gradeRepository.existsByTier(testGrade2.getTier()));
    }

    @Test
    void findAll() {
        List<Grade> allGrade = gradeRepository.findAll();
        assertEquals(allGrade.size(), 3);
    }
}