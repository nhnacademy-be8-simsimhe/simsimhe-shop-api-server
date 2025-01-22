package com.simsimbookstore.apiserver.orders.returns.service;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminReturnsService {

    Page<ReturnsResponseDto> findReturnList(Pageable pageable);

    void confirmReturn(Long orderBookId);

    void rejectReturn(Long orderBookId);
}

