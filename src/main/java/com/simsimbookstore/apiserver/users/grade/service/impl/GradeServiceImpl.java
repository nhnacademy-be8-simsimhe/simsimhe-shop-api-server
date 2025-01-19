package com.simsimbookstore.apiserver.users.grade.service.impl;

import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public Grade save(Grade grade) {
        if (gradeRepository.existsByTier(grade.getTier())) {
            throw new AlreadyExistException("already exist tier: " + grade.getTier());
        }

        return gradeRepository.save(grade);
    }

    @Override
    public Grade findByTier(Tier tier) {
        Grade grade = gradeRepository.findByTier(tier);
        if (Objects.isNull(grade)) {
            throw new NotFoundException("does not exist grade with tier : " + tier);
        }
        return grade;
    }

    @Override
    public Grade findByGradeId(Long gradeId) {
        Grade grade = gradeRepository.findByGradeId(gradeId);
        if (Objects.isNull(grade)) {
            throw new NotFoundException("does not exist grade with ID : " + gradeId);
        }
        return grade;
    }
}
