package com.simsimbookstore.apiserver.orders.returns.service;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;

import java.util.List;

public interface AdminReturnsService {

    List<ReturnsResponseDto> findReturnList();

    // 승인 버튼 > 상태 update
    // 상태, 시간, 포인트 메서드 가져와서 여기서 주는건지??
    void confirmReturn(Long orderBookId);

    void rejectReturn(Long orderBookId);
}
