package com.simsimbookstore.apiserver.orders.delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnsRequestDto {

    @NotNull(message = "배송은 필수입니다")
    private Long deliveryId;

    @NotNull(message = "책은 필수입니다.")
    private Long orderBookId;

    @NotBlank
    private String returnReason;

    @NotBlank
    @DateTimeFormat
    private LocalDateTime returnDate;

    @NotBlank
    private Integer quantity;

    @NotBlank
    @Min(value = 1, message = "수량은 1 이상이여야 합니다")
    private String returnStatus;

    @NotNull
    private Boolean refund;

    @NotNull
    private Boolean damaged;

}
