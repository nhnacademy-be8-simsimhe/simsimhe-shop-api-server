package com.simsimbookstore.apiserver.point.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyResponseDto {

    private Long pointPolicyId;
    private PointPolicy.EarningMethod earningMethod;
    private BigDecimal earningValue;

    @JsonProperty
    private String description;
    private boolean available;
    private PointPolicy.EarningType earningType;

    public static PointPolicyResponseDto fromEntity(PointPolicy entity) {
        if (entity == null) return null;
        return PointPolicyResponseDto.builder()
                .pointPolicyId(entity.getPointPolicyId())
                .earningMethod(entity.getEarningMethod())
                .earningValue(entity.getEarningValue())
                .description(entity.getDescription())
                .earningType(entity.getEarningType())
                .available(entity.getAvailable())
                .build();
    }
}
