package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.service.Impl.OrderListServiceImpl;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalServiceImpl;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller()
@RequiredArgsConstructor
public class OrderController {

    private final OrderListService orderListService;
    private final WrapTypeService wrapTypeService;
    private final OrderTotalService orderTotalService;
    private final AddressService addressService;

    @GetMapping("/api/order")
    public String orderPage(@RequestBody List<BookListRequestDto> dto, Model model) {
        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(dto);
        log.info("Book order list: {}", bookOrderList);
        List<WrapTypeResponseDto> wrapTypes = wrapTypeService.getAllWrapTypes(); // 포장지 목록 가져오기
        log.info("Wrap types: {}", wrapTypes);

        model.addAttribute("bookOrderList", bookOrderList);
        model.addAttribute("wrapTypes", wrapTypes); // 포장지 데이터 추가
        return "payTest";
    }

    @PostMapping("/api/order/total")
    @ResponseBody
    public Map<String, BigDecimal> calculateTotal(@RequestBody TotalRequestDto requestDto) {
        log.info("요청 데이터: {}", requestDto);

        // 총합 계산 요청
        BigDecimal total = orderTotalService.calculateTotal(requestDto);

        // 결과 반환
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("total", total);
        return response;
    }

}
