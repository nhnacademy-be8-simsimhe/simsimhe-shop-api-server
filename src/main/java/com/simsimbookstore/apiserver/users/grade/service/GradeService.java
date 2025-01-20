package com.simsimbookstore.apiserver.users.grade.service;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface GradeService {
    Grade save(Grade grade);

    Grade findByTier(Tier tier);

    Grade findByGradeId(Long gradeId);

    Grade calculateGrade(Long userId);
}
