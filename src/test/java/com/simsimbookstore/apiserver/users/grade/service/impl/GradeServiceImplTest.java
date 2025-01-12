package com.simsimbookstore.apiserver.users.grade.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Mock
    private GradeRepository gradeRepository;

    Grade testGrade;
    @BeforeEach
    void setUp() {
        testGrade = Grade.builder()
                .gradeId(1L)
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
    }

    @Test
    @DisplayName("새로운 등급 저장 테스트")
    void save() {
        when(gradeRepository.save(testGrade)).thenReturn(testGrade);
        gradeService.save(testGrade);

        verify(gradeRepository, times(1)).save(testGrade);
    }

    @Test
    @DisplayName("등급 티어로 조회 테스트")
    void findByTier() {
        when(gradeRepository.findByTier(Tier.STANDARD)).thenReturn(testGrade);
        gradeService.findByTier(Tier.STANDARD);

        verify(gradeRepository, times(1)).findByTier(Tier.STANDARD);
    }

    @Test
    @DisplayName("등급 아이디로 조회 테스트")
    void findByGradId(){
        when(gradeRepository.findByGradeId(testGrade.getGradeId())).thenReturn(testGrade);
        gradeService.findByGradeId(testGrade.getGradeId());

        verify(gradeRepository, times(1)).findByGradeId(testGrade.getGradeId());
    }
}