package com.simsimbookstore.apiserver.users.grade.repository;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByTier(Tier tier);

    Grade findByTier(Tier tier);

    Grade findByGradeId(Long gradeId);
}
