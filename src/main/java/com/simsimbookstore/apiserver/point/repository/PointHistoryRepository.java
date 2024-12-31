package com.simsimbookstore.apiserver.point.repository;

import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.repository.custom.PointHistoryCustomRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, PointHistoryCustomRepository {
    List<PointHistory> findByUserUserId(Long userId);

    @Query("SELECT SUM(ph.amount) FROM PointHistory ph WHERE ph.user.id = :userId")
    Integer sumAmountByUserId(@Param("userId") Long userId);
}
