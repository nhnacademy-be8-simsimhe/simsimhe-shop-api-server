package com.simsimbookstore.apiserver.orders.packages.service;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import java.util.List;

public interface WrapTypeService {

    WrapTypeResponseDto createWrapType(WrapTypeRequestDto wrapTypeRequestDto);

    WrapTypeResponseDto getWrapTypeById(Long id);

    List<WrapTypeResponseDto> getAllWrapTypes();

    WrapTypeResponseDto updateAvailability(Long id, Boolean isAvailable);

    List<WrapTypeResponseDto> getAllWarpTypeIsAvailable();
}
