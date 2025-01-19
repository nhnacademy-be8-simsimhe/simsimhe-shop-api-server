package com.simsimbookstore.apiserver.orders.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderListServiceImplTest {



    @Mock
    private BookRepository bookRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderListServiceImpl orderListService;
    
    Book mockBook1;
    Book mockBook2;

    @BeforeEach
    void setUp() {

        mockBook1 = Book.builder()
                .bookId(1L)
                .title("Test mockBook1")
                .isbn("123456789")
                .saleprice(BigDecimal.valueOf(20000))
                .build();

        mockBook2 = Book.builder()
                .bookId(2L)
                .title("Test Book")
                .isbn("123456789")
                .saleprice(BigDecimal.valueOf(10000))
                .build();
    }

    @Test
    void testToBookOrderList() {
        // given
        BookListRequestDto dto1 = new BookListRequestDto(1L, 2); // 책 ID 1, 수량 2
        BookListRequestDto dto2 = new BookListRequestDto(2L, 1); // 책 ID 2, 수량 1
        List<BookListRequestDto> requestDtos = Arrays.asList(dto1, dto2);



        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(mockBook2));

        // when
        List<BookListResponseDto> response = orderListService.toBookOrderList(requestDtos);

        // then
        assertEquals(2, response.size());

        BookListResponseDto resp1 = response.getFirst();
        assertEquals(1L, resp1.getBookId());
        assertEquals(BigDecimal.valueOf(20000), resp1.getPrice());
        assertEquals(2, resp1.getQuantity());
        assertEquals("Test mockBook1", resp1.getTitle());

        BookListResponseDto resp2 = response.get(1);
        assertEquals(2L, resp2.getBookId());
        assertEquals(BigDecimal.valueOf(10000), resp2.getPrice());
        assertEquals(1, resp2.getQuantity());
        assertEquals("Test Book", resp2.getTitle());
    }

    @Test
    void testCreateBookOrderWithCoupons() {
        // given: BookListResponseDto 데이터 생성
        BookListResponseDto dto1 = BookListResponseDto.builder()
                .bookId(1L)
                .price(BigDecimal.valueOf(10000))
                .quantity(2)
                .title("책 제목 1")
                .build();

        BookListResponseDto dto2 = BookListResponseDto.builder()
                .bookId(2L)
                .price(BigDecimal.valueOf(20000))
                .quantity(1)
                .title("책 제목 2")
                .build();

        List<BookListResponseDto> bookOrderList = Arrays.asList(dto1, dto2);
        Long userId = 123L;

        // 익명 서브클래스를 사용하여 CouponResponseDto 인스턴스 생성 및 필드 설정
        CouponResponseDto coupon1 = new CouponResponseDto() {};
        coupon1.setCouponId(10L);
        coupon1.setCouponTypeName("할인쿠폰");
        coupon1.setDisCountType(DisCountType.FIX);

        CouponResponseDto coupon2 = new CouponResponseDto() {};
        coupon2.setCouponId(20L);
        coupon2.setCouponTypeName("특별쿠폰");
        coupon2.setDisCountType(DisCountType.FIX);

        // couponService 모킹 설정
        when(couponService.getEligibleCoupons(eq(userId), eq(1L))).thenReturn(List.of(coupon1));
        when(couponService.getEligibleCoupons(eq(userId), eq(2L))).thenReturn(List.of(coupon2));

        // when: 메서드 실행
        List<BookListResponseDto> result = orderListService.createBookOrderWithCoupons(bookOrderList, userId);

        // then: 반환된 리스트가 불변 리스트인지 확인
        assertThrows(UnsupportedOperationException.class, () -> result.add(dto1));

        // 각 BookListResponseDto에 쿠폰이 올바르게 설정되었는지 확인
        assertNotNull(result.get(0).getCoupons());
        assertEquals(1, result.get(0).getCoupons().size());
        assertEquals(coupon1.getCouponId(), result.get(0).getCoupons().get(0).getCouponId());
        assertEquals(coupon1.getCouponTypeName(), result.get(0).getCoupons().get(0).getCouponTypeName());
        assertEquals(coupon1.getDisCountType(), result.get(0).getCoupons().get(0).getDiscountType());

        assertNotNull(result.get(1).getCoupons());
        assertEquals(1, result.get(1).getCoupons().size());
        assertEquals(coupon2.getCouponId(), result.get(1).getCoupons().get(0).getCouponId());
        assertEquals(coupon2.getCouponTypeName(), result.get(1).getCoupons().get(0).getCouponTypeName());
        assertEquals(coupon2.getDisCountType(), result.get(1).getCoupons().get(0).getDiscountType());
    }


}