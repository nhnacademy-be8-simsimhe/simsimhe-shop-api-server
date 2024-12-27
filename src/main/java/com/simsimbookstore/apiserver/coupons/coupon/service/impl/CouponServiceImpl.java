package com.simsimbookstore.apiserver.coupons.coupon.service.impl;

import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.mapper.CouponMapper;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponTypeRepository couponTypeRepository;

    @Override
    public CouponResponseDto getCouponById(Long couponId) {
        //couponId null 체크
        validateId(couponId);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new NotFoundException("쿠폰(id:"+couponId+")이 존재하지 않습니다."));


        return CouponMapper.toResponse(coupon);

    }

    @Override
    public Page<CouponResponseDto> getCoupons(Pageable pageable, Long userId) {
        //userId null 체크
        validateId(userId);
        //유저 확인
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:"+userId+")이 존재하지 않습니다."));

        Page<Coupon> couponPage = couponRepository.findByUserUserId(pageable, userId);

        return couponPage.map(CouponMapper::toResponse);
    }

    @Override
    public Page<CouponResponseDto> getUnusedCoupons(Pageable pageable, Long userId) {
        //userId null 체크
        validateId(userId);
        //유저 확인
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:"+userId+")이 존재하지 않습니다."));
        Page<Coupon> couponPage = couponRepository.findByUserUserIdAndCouponStatusOrderByIssueDate(pageable, userId, CouponStatus.UNUSED);
        return couponPage.map(CouponMapper::toResponse);
    }

    @Override
    public Page<CouponResponseDto> getEligibleCoupons(Pageable pageable, Long userId, Long bookId) {
        //userId null 체크
        validateId(userId);
        //bookId null 체크
        validateId(bookId);

        Page<Coupon> couponPage = couponRepository.findEligibleCouponToBook(pageable, userId, bookId);
        return couponPage.map(CouponMapper::toResponse);
    }

    @Override
    @Transactional
    public List<CouponResponseDto> IssueCoupons(List<Long> userIds, Long couponTypeId) {

        CouponType couponType = couponTypeRepository.findById(couponTypeId).orElseThrow(() -> new NotFoundException("쿠폰 정책(id:" + couponTypeId + ")이 존재하지 않습니다.1"));

        // 회원 존재 확인
        for (Long userId : userIds) {
            validateId(userId);
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));

            List<Coupon> unusedCoupons = couponRepository.findUnusedCouponByUserAndType(userId, couponTypeId);

//            if(unusedCoupons.isEmpty())
        };


        return null;





    }

    @Override
    public List<CouponResponseDto> expireCoupon(Long userId, Long couponId) {
        return null;
    }

    @Override
    public List<CouponResponseDto> useCoupon(Long userId, Long couponId) {
        return null;
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {

    }

    public void validateId(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID가 null 입니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID는 0보다 커야합니다.");
        }
    }
}
