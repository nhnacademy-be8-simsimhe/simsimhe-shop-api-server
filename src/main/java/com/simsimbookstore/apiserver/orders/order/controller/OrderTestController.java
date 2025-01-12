package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderCouponResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.Impl.OrderListServiceImpl;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.impl.WrapTypeServiceImpl;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.users.address.dto.AddressResponseDto;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CouponService couponService;

    @GetMapping("/api/test/order")
    public String testOrderPage(Model model) {
        Long userId = 1L; // 테스트용 userId

        // 테스트용 BookListRequestDto 데이터 생성
        List<BookListRequestDto> testDtoList = List.of(
                new BookListRequestDto(1L, 2),
                new BookListRequestDto(2L, 1)
        );

        // 책 주문 리스트 생성 및 쿠폰 포함
        List<BookListResponseDto> bookOrderList = orderListService.createBookOrderWithCoupons(
                orderListService.toBookOrderList(testDtoList),
                userId
        );

        log.info("Book Order List: {}", bookOrderList);
        for (BookListResponseDto book : bookOrderList) {
            log.info("Book ID: {}, Coupons: {}", book.getBookId(), book.getCoupons());
        }


        // 추가 데이터
        List<WrapTypeResponseDto> wrapTypes = wrapTypeService.getAllWrapTypes();
        List<AddressResponseDto> addresses = addressService.getAddresses(userId);
        BigDecimal userPoints = pointHistoryService.getUserPoints(userId);

        // 모델에 데이터 추가
        model.addAttribute("userId", userId);
        model.addAttribute("bookOrderList", bookOrderList); // 쿠폰 포함된 책 리스트
        model.addAttribute("wrapTypes", wrapTypes);
        model.addAttribute("addresses", addresses);
        model.addAttribute("availablePoints", userPoints);

        return "payTest"; // payTest.html 렌더링
    }
}
