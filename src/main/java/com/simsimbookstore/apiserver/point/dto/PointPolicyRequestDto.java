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
public class PointPolicyRequestDto {

    private PointPolicy.EarningMethod earningMethod;
    private BigDecimal earningValue;
    private String description;
    private boolean available;
    private PointPolicy.EarningType earningType;

    // DTO -> 엔티티 변환
    public PointPolicy toEntity() {
        return PointPolicy.builder()
                .earningMethod(this.earningMethod)
                .earningValue(this.earningValue)
                .description(this.description)
                .earningType(this.earningType)
                .available(this.available)
                .build();
    }
}
