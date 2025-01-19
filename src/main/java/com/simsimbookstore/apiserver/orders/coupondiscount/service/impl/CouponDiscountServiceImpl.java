package com.simsimbookstore.apiserver.orders.coupondiscount.service.impl;

import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.UserCouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.coupondiscount.service.CouponDiscountService;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponDiscountServiceImpl implements CouponDiscountService {

    private final CouponDiscountRepository couponDiscountRepository;
    private final CouponRepository couponRepository;

    /**
     * 쿠폰 할인 생성
     */
    @Override
    @Transactional
    public CouponDiscountResponseDto createCouponDiscount(CouponDiscountRequestDto requestDto, OrderBook orderBook) {
        // (1) 기존 쿠폰 삭제(선택적)
        if (orderBook.getCouponDiscount() != null) {
            couponDiscountRepository.delete(orderBook.getCouponDiscount());
            orderBook.setCouponDiscount(null);
        }

        // (2) 새 쿠폰 할인 생성
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .coupon(couponRepository.findById(requestDto.getCouponId()).orElseThrow())
                .orderBook(orderBook)
                .couponName(requestDto.getCouponName())
                .couponType(requestDto.getCouponType())
                .discountPrice(requestDto.getDiscountPrice())
                .build();

        // (3) 양방향 연결
        orderBook.setCouponDiscount(couponDiscount);

        // (4) 저장
        return toResponseDto(couponDiscountRepository.save(couponDiscount));
    }

    /**
     * 쿠폰 할인 조회
     */
    @Override
    public CouponDiscountResponseDto findById(Long id) {
        CouponDiscount couponDiscount = couponDiscountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CouponDiscount not found"));

        return toResponseDto(couponDiscount);
    }

    /**
     * 쿠폰 할인 삭제
     */

    @Override
    @Transactional
    public void deleteById(Long id) {
        CouponDiscount couponDiscount = couponDiscountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CouponDiscount not found"));

        // OrderBook과의 관계 해제
        couponDiscount.getOrderBook().setCouponDiscount(null);

        couponDiscountRepository.delete(couponDiscount);
    }

    @Override
    public Page<UserCouponDiscountResponseDto> getUserCouponDiscount(Long userId, Pageable pageable) {
        Page<CouponDiscount> couponDiscountPage = couponDiscountRepository.getUserCouponDiscount(userId, pageable);
        return couponDiscountPage.map(this::toUserCouponDiscountResponseDto);
    }

    /**
     * 엔티티를 ResponseDto로 변환
     */
    private CouponDiscountResponseDto toResponseDto(CouponDiscount couponDiscount) {
        return CouponDiscountResponseDto.builder()
                .couponDiscountId(couponDiscount.getCouponDiscountId())
                .orderBookId(couponDiscount.getOrderBook().getOrderBookId())
                .couponName(couponDiscount.getCouponName())
                .couponType(couponDiscount.getCouponType())
                .discountPrice(couponDiscount.getDiscountPrice())
                .build();
    }

    private UserCouponDiscountResponseDto toUserCouponDiscountResponseDto(CouponDiscount couponDiscount) {
        BigDecimal discountPrice = couponDiscount.getDiscountPrice();
        String couponName = couponDiscount.getCouponName();
        String bookTitle = couponDiscount.getOrderBook().getBook().getTitle();
        LocalDateTime useDate = couponDiscount.getCoupon().getUseDate();
        return UserCouponDiscountResponseDto.builder()
                .discountPrice(discountPrice)
                .couponName(couponName)
                .useDate(useDate)
                .bookTitle(bookTitle)
                .build();
    }
}

