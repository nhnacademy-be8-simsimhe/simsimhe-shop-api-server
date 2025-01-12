package com.simsimbookstore.apiserver.orders.packages.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import com.simsimbookstore.apiserver.orders.packages.service.impl.WrapTypeServiceImpl;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WrapTypeServiceImplTest {

    @Mock
    private WrapTypeRepository wrapTypeRepository;

    @InjectMocks
    private WrapTypeServiceImpl wrapTypeService;



    @Test
    @DisplayName("새로운 WrapType 생성 성공 테스트")
    void createWrapTypeSuccess() {
        WrapTypeRequestDto requestDto = new WrapTypeRequestDto();
        requestDto.setPackageName("Gift Wrap");
        requestDto.setPackagePrice(BigDecimal.valueOf(1000));

        WrapType savedWrapType = WrapType.builder()
                .packageTypeId(1L)
                .packageName("Gift Wrap")
                .packagePrice(BigDecimal.valueOf(1000))
                .build();

        when(wrapTypeRepository.save(any(WrapType.class))).thenReturn(savedWrapType);

        WrapTypeResponseDto responseDto = wrapTypeService.createWrapType(requestDto);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getPackageTypeId());
        assertEquals("Gift Wrap", responseDto.getPackageName());
        assertEquals(BigDecimal.valueOf(1000), responseDto.getPackagePrice());

        verify(wrapTypeRepository, times(1)).save(any(WrapType.class));
    }

    @Test
    @DisplayName("WrapType ID로 조회 성공 테스트")
    void getWrapTypeByIdSuccess() {
        Long wrapTypeId = 1L;
        WrapType wrapType = WrapType.builder()
                .packageTypeId(1L)
                .packageName("Gift Wrap")
                .packagePrice(BigDecimal.valueOf(1500))
                .build();
        when(wrapTypeRepository.findById(wrapTypeId)).thenReturn(Optional.of(wrapType));

        WrapTypeResponseDto responseDto = wrapTypeService.getWrapTypeById(wrapTypeId);

        assertNotNull(responseDto);
        assertEquals(wrapTypeId, responseDto.getPackageTypeId());
        assertEquals("Gift Wrap", responseDto.getPackageName());
        assertEquals(BigDecimal.valueOf(1500), responseDto.getPackagePrice());

        verify(wrapTypeRepository, times(1)).findById(wrapTypeId);
    }

    @Test
    @DisplayName("WrapType ID로 조회 실패 테스트")
    void getWrapTypeByIdNotFound() {
        Long wrapTypeId = 1L;

        when(wrapTypeRepository.findById(wrapTypeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wrapTypeService.getWrapTypeById(wrapTypeId)
        );

        assertEquals("WrapType not found with ID: " + wrapTypeId, exception.getMessage());
        verify(wrapTypeRepository, times(1)).findById(wrapTypeId);
    }

    @Test
    @DisplayName("WrapType 전체 목록 조회 테스트")
    void getAllWrapTypesSuccess() {
        WrapType wrapType1 = WrapType.builder()
                .packageTypeId(1L)
                .packageName("Gift Wrap")
                .packagePrice(BigDecimal.valueOf(1000))
                .build();

        WrapType wrapType2 = WrapType.builder()
                .packageTypeId(2L)
                .packageName("Premium Wrap")
                .packagePrice(BigDecimal.valueOf(1500))
                .build();

        when(wrapTypeRepository.findAll()).thenReturn(Arrays.asList(wrapType1, wrapType2));

        List<WrapTypeResponseDto> responseDtos = wrapTypeService.getAllWrapTypes();

        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertEquals("Gift Wrap", responseDtos.get(0).getPackageName());
        assertEquals("Premium Wrap", responseDtos.get(1).getPackageName());

        verify(wrapTypeRepository, times(1)).findAll();
    }
}