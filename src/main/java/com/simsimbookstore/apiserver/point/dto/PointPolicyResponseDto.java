package com.simsimbookstore.apiserver.point.dto;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyResponseDto {

    private PointPolicy.EarningMethod earningMethod;
    private BigDecimal earningValue;
    private String description;
    private boolean isAvailable;
    private PointPolicy.EarningType earningType;

    public static PointPolicyResponseDto fromEntity(PointPolicy entity) {
        if (entity == null) return null;
        return PointPolicyResponseDto.builder()
                .earningMethod(entity.getEarningMethod())
                .earningValue(entity.getEarningValue())
                .description(entity.getDescription())
                .earningType(entity.getEarningType())
                //.isAvailable(entity.getIsAvailable())
                .build();
    }
}
