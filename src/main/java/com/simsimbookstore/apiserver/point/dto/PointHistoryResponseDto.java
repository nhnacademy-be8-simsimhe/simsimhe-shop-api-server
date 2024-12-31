package com.simsimbookstore.apiserver.point.dto;

import com.simsimbookstore.apiserver.point.entity.PointHistory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryResponseDto {
    private PointHistory.PointType pointType;
    private Integer amount;
    private LocalDateTime createdAt;
    private String sourceType;
    private Long sourceId;                   //review_id OR order_id
    private String description;
}
