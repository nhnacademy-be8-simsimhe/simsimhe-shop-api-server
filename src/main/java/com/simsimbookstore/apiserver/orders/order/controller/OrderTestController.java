package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.Impl.OrderListServiceImpl;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.impl.WrapTypeServiceImpl;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderTestController {

    private final OrderListServiceImpl orderListService;
    private final WrapTypeServiceImpl wrapTypeService;
    private final AddressService addressService;
    private final PointHistoryService pointHistoryService;

    @GetMapping("/api/test/order")
    public String testOrderPage(Model model) {
        Long userId = 1L; // 고정된 테스트용 userId
        log.info("testOrderPage 호출 시 userId: {}", userId);
        // 테스트용 BookListRequestDto 데이터 생성
        List<BookListRequestDto> testDtoList = new ArrayList<>();
        testDtoList.add(new BookListRequestDto(1L, 2)); // bookId=1, quantity=2
        testDtoList.add(new BookListRequestDto(2L, 1)); // bookId=2, quantity=1

        // orderPage 메서드에 전달
        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(testDtoList);
        List<WrapTypeResponseDto> wrapTypes = wrapTypeService.getAllWrapTypes();
        List<AddressResponseDto> addresses = addressService.getAddresses(userId);
        BigDecimal userPoints = pointHistoryService.getUserPoints(userId);
        for (AddressResponseDto a : addresses) {
            log.info("Addresses: {}", a.getRoadAddress());
        }
        model.addAttribute("userId", userId);
        model.addAttribute("bookOrderList", bookOrderList);
        model.addAttribute("wrapTypes", wrapTypes);
        model.addAttribute("addresses", addresses);
        model.addAttribute("availablePoints", userPoints);
        return "payTest"; // payTest.html 렌더링
    }
}
