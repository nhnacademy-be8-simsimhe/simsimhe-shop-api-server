package com.simsimbookstore.apiserver.orders.packages.dto;

import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class WrapTypeRequestDto {

    @NotBlank(message = "포장지 이름을 공백없이 입력해 주세요")
    @Length(max = 50)
    private String packageName;

    @NotNull(message = "가격은 필수 사항 입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private BigDecimal packagePrice;

    @NotNull(message = "판매 가능 여부는 필수 사항입니다.")
    private Boolean isAvailable;

    public WrapType toEntity() {
        return WrapType.builder()
                .packageName(this.packageName)
                .packagePrice(this.packagePrice)
                .isAvailable(this.isAvailable)
                .build();
    }
}

