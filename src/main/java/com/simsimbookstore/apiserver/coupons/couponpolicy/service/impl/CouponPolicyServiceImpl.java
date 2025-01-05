package com.simsimbookstore.apiserver.coupons.couponpolicy.service.impl;

import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.mapper.CouponPolicyMapper;
import com.simsimbookstore.apiserver.coupons.couponpolicy.repository.CouponPolicyRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.service.CouponPolicyService;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.coupons.exception.AlreadyCouponPolicyUsed;
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
public class CouponPolicyServiceImpl implements CouponPolicyService {
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponTypeRepository couponTypeRepository;

    @Override
    public Page<CouponPolicyResponseDto> getAllCouponPolicy(Pageable pageable) {
        Page<CouponPolicy> couponPolicies = couponPolicyRepository.findAll(pageable);
        return couponPolicies.map(CouponPolicyMapper::toResponse);
    }

    @Override
    public CouponPolicyResponseDto getCouponPolicy(Long couponPolicyId) {
        validateId(couponPolicyId);
        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId).orElseThrow(() -> new NotFoundException("쿠폰정책(id:" + couponPolicyId + ")이 존재하지 않습니다."));
        return CouponPolicyMapper.toResponse(couponPolicy);

    }

    @Override
    @Transactional
    public CouponPolicyResponseDto createCouponPolicy(CouponPolicyRequestDto requestDto) {
        CouponPolicy couponPolicy = CouponPolicyMapper.toCouponPolicy(requestDto);
        CouponPolicy save = couponPolicyRepository.save(couponPolicy);
        return CouponPolicyMapper.toResponse(save);
    }


    @Override
    @Transactional
    public void deleteCouponPolicy(Long couponPolicyId) {
        validateId(couponPolicyId);
        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId).orElseThrow(() -> new NotFoundException("쿠폰정책(id:" + couponPolicyId + ")이 존재하지 않습니다."));
        List<CouponType> couponTypes = couponTypeRepository.findByCouponPolicyCouponPolicyId(couponPolicyId);
        if (!couponTypes.isEmpty()) {
            throw new AlreadyCouponPolicyUsed("쿠폰정책(id:" + couponPolicyId + ")으로 쿠폰 타입이 생성되었습니다.");
        }
        couponPolicyRepository.delete(couponPolicy);
    }

    private void validateId(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID가 null 입니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID는 0보다 커야합니다.");
        }
    }
//    @Override
//    public CouponPolicyResponseDto updateCouponPolicy(Long couponPolicyId, CouponPolicyRequestDto requestDto) {
//        validateId(couponPolicyId);
//        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId).orElseThrow(() -> new NotFoundException("쿠폰정책(id:" + couponPolicyId + ")이 존재하지 않습니다."));
//        couponPolicy.setCouponPolicyName(requestDto.getCouponPolicyName());
//        couponPolicy.setDiscountType(requestDto.);
//        return null;
//    }
}
