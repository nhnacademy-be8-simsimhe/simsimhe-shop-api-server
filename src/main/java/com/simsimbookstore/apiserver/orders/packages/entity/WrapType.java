package com.simsimbookstore.apiserver.orders.packages.entity;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wrap_types")
public class WrapType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageTypeId;

    @Column(name = "wrap_name", nullable = false, length = 100)
    private String packageName;

    @Column(name = "wrap_price", nullable = false)
    private BigDecimal packagePrice;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;


    public void updateAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public WrapTypeResponseDto toResponseDto() {
        return WrapTypeResponseDto.builder()
                .packageTypeId(this.packageTypeId)
                .packageName(this.packageName)
                .packagePrice(this.packagePrice)
                .isAvailable(this.isAvailable)
                .build();
    }
}
