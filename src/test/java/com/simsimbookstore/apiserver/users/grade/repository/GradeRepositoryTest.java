package com.simsimbookstore.apiserver.users.grade.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    private Grade testGrade;

    @BeforeEach
    void setUp() {
        testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        gradeRepository.save(testGrade);
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
    void findByGradeId(){
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
        assertFalse(gradeRepository.existsByTier(Tier.ROYAL));
    }
}