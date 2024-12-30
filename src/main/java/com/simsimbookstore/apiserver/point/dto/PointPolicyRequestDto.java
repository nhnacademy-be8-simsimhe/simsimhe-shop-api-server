package com.simsimbookstore.apiserver.point.dto;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PointPolicyRequestDto {
    PointPolicy.EarningType earningType;
    Integer fixPoints;
    BigDecimal rating;
    String description;
    LocalDateTime createdAt;
    PointPolicy.EarningForm earningForm;
}
