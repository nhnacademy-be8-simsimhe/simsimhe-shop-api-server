package com.simsimbookstore.apiserver.orders.returns.service;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsRequestDto;
import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.returns.entity.Returns;

public interface ReturnsService {
    void createReturn(ReturnsRequestDto requestDto);

    ReturnsResponseDto updateReturnStatus(Long returnId, Returns.ReturnState newState);

    ReturnsResponseDto getReturnById(Long returnId);

    void deleteReturn(Long returnId);
}
