package com.simsimbookstore.apiserver.point.repository;

import com.simsimbookstore.apiserver.point.entity.PointHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserUserId(Long userId);
    Integer sumAmountByUser_UserId(Long userId);
}
