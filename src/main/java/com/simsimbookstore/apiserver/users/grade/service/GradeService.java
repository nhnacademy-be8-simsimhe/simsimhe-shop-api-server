package com.simsimbookstore.apiserver.users.grade.service;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import lombok.AllArgsConstructor;

public interface GradeService {
    Grade save(Grade grade);

    Grade findByTier(Tier tier);

    Grade findByGradeId(Long gradeId);
}
