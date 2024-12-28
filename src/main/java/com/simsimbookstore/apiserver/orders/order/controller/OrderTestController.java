package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.Impl.OrderListServiceImpl;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.impl.WrapTypeServiceImpl;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import com.simsimbookstore.apiserver.users.address.service.impl.AddressServiceImpl;
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
    //private final AddressService addressService;


    @GetMapping("/api/test/order")
    public String testOrderPage(Model model) {
        // 테스트용 BookListRequestDto 데이터 생성
        List<BookListRequestDto> testDtoList = new ArrayList<>();
        testDtoList.add(new BookListRequestDto(1L, 2)); // bookId=1, quantity=2
        testDtoList.add(new BookListRequestDto(2L, 1)); // bookId=2, quantity=1

        // orderPage 메서드에 전달
        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(testDtoList);
        List<WrapTypeResponseDto> wrapTypes = wrapTypeService.getAllWrapTypes();
//        List<Address> addresses = addressService.getAddresses(1L);
//        for (Address a : addresses) {
//            log.info("Addresses: {}", a.getRoadAddress());
//        }

        model.addAttribute("bookOrderList", bookOrderList);
        model.addAttribute("wrapTypes", wrapTypes);
       // model.addAttribute("addresses", addresses);
        return "payTest"; // payTest.html 렌더링
    }
}
