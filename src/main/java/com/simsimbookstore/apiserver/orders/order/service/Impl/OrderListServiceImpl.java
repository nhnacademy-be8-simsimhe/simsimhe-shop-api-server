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
        for (BookListResponseDto book : bookOrderList) {
            // 쿠폰 가져오기
            Page<CouponResponseDto> couponPage = couponService.getEligibleCoupons(
                    PageRequest.of(0, 100), // 최대 100개 쿠폰 조회
                    userId,
                    book.getBookId()
            );

            // 쿠폰 데이터를 변환하여 추가
            List<OrderCouponResponseDto> coupons = couponPage.getContent().stream()
                    .map(coupon -> new OrderCouponResponseDto(coupon.getCouponId(), coupon.getCouponTypeName(), coupon.getDisCountType()))
                    .collect(Collectors.toList());

            book.setCoupons(coupons); // 쿠폰 설정 (빈 리스트라도 반드시 설정)
        }

        return bookOrderList; // 쿠폰이 포함된 책 리스트 반환
}

}
