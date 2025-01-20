package com.simsimbookstore.apiserver.users.grade.service.impl;

import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.grade.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;



@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final OrderRepository orderRepository;

    @Transactional
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

    @Transactional
    @Override
    public Grade calculateGrade(Long userId){
        List<Order> orders = orderRepository.findAllByUserUserId(userId);

        BigDecimal userTotalPrice = BigDecimal.ZERO;
        for (Order orderItem : orders) {
            userTotalPrice = userTotalPrice.add(orderItem.getTotalPrice());
        }

        List<Grade> gradeAll = gradeRepository.findAll();

        if (gradeAll.isEmpty()) {
            throw new NotFoundException("grade list is empty");
        }

        Grade newGrade = gradeAll.getFirst();
        for (Grade grade : gradeAll) {
            if (userTotalPrice.compareTo(grade.getMinAmount())  > 0){
                newGrade = grade;
            }
        }
        return newGrade;
    }
}
