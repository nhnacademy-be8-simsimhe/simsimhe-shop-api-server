package com.simsimbookstore.apiserver.orders.delivery.service;

import com.simsimbookstore.apiserver.orders.delivery.dto.ReturnsRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Returns;

public interface ReturnsService {
    ReturnsResponseDto createReturn(ReturnsRequestDto requestDto);

    ReturnsResponseDto updateReturnStatus(Long returnId, Returns.ReturnState newState);

    ReturnsResponseDto getReturnById(Long returnId);

    void deleteReturn(Long returnId);
}
