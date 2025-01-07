package com.simsimbookstore.apiserver.coupons.coupontype.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.repository.CouponPolicyRepository;
import com.simsimbookstore.apiserver.coupons.exception.AlreadyCouponTypeIssue;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.mapper.CouponTypeMapper;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.coupons.coupontype.service.CouponTypeService;
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
public class CouponTypeServiceImpl implements CouponTypeService {
    private final CouponTypeRepository couponTypeRepository;
    private final CouponRepository couponRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 모든 쿠폰 타입을 가져온다.
     * @param pageable
     * @return 쿠폰타입 페이지
     */
    @Override
    public Page<CouponTypeResponseDto> getAllCouponType(Pageable pageable) {
        Page<CouponType> couponTypes = couponTypeRepository.findAll(pageable);
        return couponTypes.map(CouponTypeMapper::toResponse);
    }

    /**
     * 특정 쿠폰타입을 가져온다.
     * @param couponTypeId
     * @throws NotFoundException 쿠폰타입이 존재하지 않을 경우
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @return
     */
    @Override
    public CouponTypeResponseDto getCouponType(Long couponTypeId) {
        validateId(couponTypeId);
        CouponType couponType = couponTypeRepository.findById(couponTypeId).orElseThrow(() -> new NotFoundException("쿠폰타입(id:" + couponTypeId + ")이 존재하지 않습니다."));
        return CouponTypeMapper.toResponse(couponType);

    }

    /**
     * 특정 쿠폰 정책과 연결된 쿠폰 타입들을 가져온다.
     * @param pageable
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @param couponPolicyId
     * @return
     */
    @Override
    public Page<CouponTypeResponseDto> getCouponByCouponPolicy(Pageable pageable, Long couponPolicyId) {
        validateId(couponPolicyId);
        Page<CouponType> couponTypePage = couponTypeRepository.findByCouponPolicyCouponPolicyId(pageable, couponPolicyId);
        return couponTypePage.map(CouponTypeMapper::toResponse);
    }

    /**
     * 쿠폰 타입을 생성한다.
     * BookCoupon인지 CategoryCoupon인지 AllCoupon인지 확인 후 저장한다.
     * @param requestDto
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 책,쿠폰타입이 존재하지 않을 경우
     * @return 저장한 쿠폰 타입의 reponseDto
     */
    @Override
    @Transactional
    public CouponTypeResponseDto createCouponType(CouponTypeRequestDto requestDto) {
        CouponType couponType = CouponTypeMapper.toCouponType(requestDto);
        if (couponType instanceof BookCoupon) {
            validateId(requestDto.getTargetId());
            Book book = bookRepository.findByBookId(requestDto.getTargetId()).orElseThrow(() -> new NotFoundException("책(id:" + requestDto.getTargetId() + ")이 존재하지 않습니다."));
            ((BookCoupon) couponType).setBook(book);
        } else if (couponType instanceof CategoryCoupon) {
            validateId(requestDto.getTargetId());
            Category category = categoryRepository.findById(requestDto.getTargetId()).orElseThrow(() -> new NotFoundException("카테고리(id:" + requestDto.getTargetId() + ")이 존재하지 않습니다."));
            ((CategoryCoupon) couponType).setCategory(category);
        }
        validateId(requestDto.getCouponPolicyId());
        CouponPolicy couponPolicy = couponPolicyRepository.findById(requestDto.getCouponPolicyId()).orElseThrow(() -> new NotFoundException("쿠폰 정책(id:" + requestDto.getCouponPolicyId() + ")이 존재하지 않습니다."));
        couponType.setCouponPolicy(couponPolicy);
        CouponType save = couponTypeRepository.save(couponType);
        return CouponTypeMapper.toResponse(save);
    }

    /**
     * 특정 쿠폰 타입을 삭제한다.
     * 쿠폰 타입이 회원들에게 발급되면 삭제할 수 없다.
     * @param couponTypeId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 책,쿠폰타입이 존재하지 않을 경우
     * @throws AlreadyCouponTypeIssue 쿠폰 타입이 이미 회원들에게 발급되었을 때
     */
    @Override
    @Transactional
    public void deleteCouponType(Long couponTypeId) {
        validateId(couponTypeId);
        CouponType couponType = couponTypeRepository.findById(couponTypeId).orElseThrow(() -> new NotFoundException("쿠폰타입(id:" + couponTypeId + ")이 존재하지 않습니다."));
        List<Coupon> coupons = couponRepository.findByCouponTypeCouponTypeId(couponTypeId);
        if (!coupons.isEmpty()) {
            throw new AlreadyCouponTypeIssue("쿠폰 타입(id:" + couponTypeId + ")이 이미 회원에게 발급되었습니다.");
        }
        couponTypeRepository.delete(couponType);
    }

    /**
     * Id값의 유효성을 체크한다.
     * @param id
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     */
    private void validateId(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID가 null 입니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID는 0보다 커야합니다.");
        }
    }
}
