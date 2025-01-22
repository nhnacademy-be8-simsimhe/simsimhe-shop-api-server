package com.simsimbookstore.apiserver.orders.packages.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class WrapTypeDtoTest {

    @Test
    void testBuilderAndGetters() {
        // Builder를 사용하여 객체 생성
        PackageResponseDto dto = PackageResponseDto.builder()
                .packageId(200L)
                .packageType("Gift Package")
                .build();

        // getter를 통해 설정된 값 검증
        assertEquals(200L, dto.getPackageId());
        assertEquals("Gift Package", dto.getPackageType());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // NoArgs 생성자를 사용하여 객체 생성
        WrapTypeResponseDto dto = new WrapTypeResponseDto();

        // setter를 이용해 필드 값 설정
        dto.setPackageTypeId(1L);
        dto.setPackageName("Gift Wrap");
        dto.setPackagePrice(BigDecimal.valueOf(19.99));
        dto.setIsAvailable(true);

        // getter를 통해 설정한 값들이 올바르게 반환되는지 검증
        assertEquals(1L, dto.getPackageTypeId());
        assertEquals("Gift Wrap", dto.getPackageName());
        assertEquals(BigDecimal.valueOf(19.99), dto.getPackagePrice());
        assertTrue(dto.getIsAvailable());
    }
    @Test
    void testNoArgsConstructor() {
        // NoArgs 생성자를 사용하여 객체 생성
        PackageRequestDto dto = new PackageRequestDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getPackageTypeId(), "packageTypeId는 기본적으로 null이어야 합니다.");
        assertNull(dto.getPackageName(), "packageName은 기본적으로 null이어야 합니다.");
    }

    @Test
    void testResponseNoArgsConstructor() {
        // NoArgs 생성자를 사용하여 객체 생성
        PackageResponseDto dto = new PackageResponseDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getPackageId(), "packageId는 기본적으로 null이어야 합니다.");
        assertNull(dto.getPackageType(), "packageType은 기본적으로 null이어야 합니다.");
    }

}