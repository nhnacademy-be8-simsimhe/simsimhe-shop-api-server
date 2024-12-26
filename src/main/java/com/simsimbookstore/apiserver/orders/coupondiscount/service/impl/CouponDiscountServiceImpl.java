package com.simsimbookstore.apiserver.orders.coupondiscount.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.coupondiscount.service.CouponDiscountService;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponDiscountServiceImpl implements CouponDiscountService {

    private final CouponDiscountRepository couponDiscountRepository;
    private final OrderBookRepository orderBookRepository;

    /**
     * 쿠폰 할인 생성
     */
    @Transactional
    @Override
    public CouponDiscountResponseDto createCouponDiscount(CouponDiscountRequestDto requestDto) {
        OrderBook orderBook = orderBookRepository.findById(requestDto.getOrderBookId())
                .orElseThrow(() -> new NotFoundException("OrderBook not found"));

        // (OrderBook당 하나의 쿠폰 할인만 허용)
        if (orderBook.getCouponDiscount() != null) {
            couponDiscountRepository.delete(orderBook.getCouponDiscount());
            orderBook.setCouponDiscount(null);
        }

        // 새로운 CouponDiscount 생성
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .orderBook(orderBook)
                .couponName(requestDto.getCounponName())
                .couponType(requestDto.getCouponType())
                .discountPrice(requestDto.getDiscountPrice())
                .build();

        // OrderBook과의 관계 설정
        orderBook.setCouponDiscount(couponDiscount);

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
    @Transactional
    @Override
    public void deleteById(Long id) {
        CouponDiscount couponDiscount = couponDiscountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CouponDiscount not found"));

        // OrderBook과의 관계 해제
        couponDiscount.getOrderBook().setCouponDiscount(null);

        couponDiscountRepository.delete(couponDiscount);
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
}
