package com.simsimbookstore.apiserver.orders.order.service.Impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderCouponResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderListServiceImpl implements OrderListService {

    private final BookRepository bookRepository;
    private final CouponService couponService;

    @Override
    public List<BookListResponseDto> toBookOrderList(List<BookListRequestDto> dtos) {
        List<BookListResponseDto> bookListResponseDtos = new ArrayList<>();
        for (BookListRequestDto dto : dtos) {
            Book book = bookRepository.findById(dto.getBookId()).orElseThrow();
            BookListResponseDto repDto = BookListResponseDto.builder()
                    .bookId(dto.getBookId())
                    .price(book.getSaleprice())
                    .quantity(dto.getQuantity())
                    .title(book.getTitle())
                    .build();

            bookListResponseDtos.add(repDto);
        }

        return bookListResponseDtos;
    }

    @Override
    public List<BookListResponseDto> createBookOrderWithCoupons(List<BookListResponseDto> bookOrderList, Long userId) {
        return bookOrderList.stream()
                .peek(book -> {
                    // 페이지 요청 없이 쿠폰 조회
                    List<CouponResponseDto> eligibleCoupons = couponService.getEligibleCoupons(userId, book.getBookId());

                    // 쿠폰 데이터를 변환하여 OrderCouponResponseDto 리스트 생성
                    List<OrderCouponResponseDto> coupons = eligibleCoupons.stream()
                            .map(coupon -> new OrderCouponResponseDto(
                                    coupon.getCouponId(),
                                    coupon.getCouponTypeName(),
                                    coupon.getDisCountType()))
                            .collect(Collectors.toList());

                    book.setCoupons(coupons);  // 쿠폰 설정
                })
                .collect(Collectors.toList());
    }

}
