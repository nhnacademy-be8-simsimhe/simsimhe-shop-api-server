package com.simsimbookstore.apiserver.coupons.coupon.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.bookcategory.repository.BookCategoryRepository;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.DiscountAmountResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.EmptyCouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.exception.AlreadyCouponUsed;
import com.simsimbookstore.apiserver.coupons.exception.InapplicableCoupon;
import com.simsimbookstore.apiserver.coupons.exception.InsufficientOrderAmountException;
import com.simsimbookstore.apiserver.coupons.coupon.mapper.CouponMapper;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final BookCategoryRepository bookCategoryRepository;

    /**
     * couponId로 쿠폰을 가져온다.
     * @param couponId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @return 쿠폰
     */
    @Override
    public CouponResponseDto getCouponById(Long couponId) {
        //couponId null 체크
        validateId(couponId);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new NotFoundException("쿠폰(id:"+couponId+")이 존재하지 않습니다."));


        return CouponMapper.toResponse(coupon);

    }

    /**
     * 유저가 가지고 있는 쿠폰 중 특정 쿠폰 타입이고 아직 미사용인 쿠폰을 가지고 온다.
     * 각 유저는 미사용된 특정 쿠폰 타입의 쿠폰을 하나씩만 가지고 있을 수 있음.
     * @param userId
     * @param couponTypeId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 쿠폰, 회원이 존재하지 않을 경우
     * @return 미사용된 쿠폰
     */
    @Override
    public CouponResponseDto getUnusedCouponByCouponType(Long userId, Long couponTypeId) {
        validateId(userId);
        validateId(couponTypeId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));
        couponTypeRepository.findById(couponTypeId).orElseThrow(() -> new NotFoundException("쿠폰 정책(id:" + couponTypeId + ")이 존재하지 않습니다."));
        Optional<Coupon> unusedCoupon = couponRepository.findUnusedCouponByUserAndType(userId, couponTypeId);
        if (unusedCoupon.isPresent()) {
            return CouponMapper.toResponse(unusedCoupon.get());
        }
        return new EmptyCouponResponseDto();

    }

    /**
     * 유저가 가진 쿠폰을 Page로 가지고 온다
     * 만료,미사용,사용된 쿠폰 상관없이 가져온다.
     * @param pageable
     * @param userId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원이 존재하지 않을 경우
     * @return 유저의 쿠폰 페이지
     */
    @Override
    public Page<CouponResponseDto> getCoupons(Pageable pageable, Long userId) {
        //userId null 체크
        validateId(userId);
        //유저 확인
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:"+userId+")이 존재하지 않습니다."));

        Page<Coupon> couponPage = couponRepository.findByUserUserId(pageable, userId);

        return couponPage.map(CouponMapper::toResponse);
    }

    /**
     * 유저의 쿠폰 중 미사용된 쿠폰을 Page로 가져온다.
     * @param pageable
     * @param userId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원이 존재하지 않을 경우

     * @return 미사용된 쿠폰 페이지
     */
    @Override
    public Page<CouponResponseDto> getUnusedCoupons(Pageable pageable, Long userId) {
        //userId null 체크
        validateId(userId);
        //유저 확인
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:"+userId+")이 존재하지 않습니다."));
        Page<Coupon> couponPage = couponRepository.findByUserUserIdAndCouponStatus(pageable, userId, CouponStatus.UNUSED);
        return couponPage.map(CouponMapper::toResponse);
    }

    /**
     * 유저가 가지고 있는 쿠폰 중 특정 책에 적용 가능한 쿠폰을 Page로 가져온다.
     * @param pageable
     * @param userId
     * @param bookId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원, 도서가 존재하지 않을 경우
     * @return 적용가능한 쿠폰 패이지
     */
    @Override
    public Page<CouponResponseDto> getEligibleCoupons(Pageable pageable, Long userId, Long bookId) {
        //userId null 체크
        validateId(userId);
        //bookId null 체크
        validateId(bookId);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));
        bookRepository.findByBookId(bookId).orElseThrow(() -> new NotFoundException("도서(id:" + bookId + ")이 존재하지 않습니다."));

        Page<Coupon> couponPage = couponRepository.findEligibleCouponToBook(pageable, userId, bookId);
        return couponPage.map(CouponMapper::toResponse);
    }

    /**
     * 유저들에게 쿠폰을 발행한다.
     * @param userIds
     * @param couponTypeId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원,쿠폰타입이 존재하지 않을 경우
     * @throws AlreadyExistException 유저가 미사용된 특정 쿠폰타입의 쿠폰 이미 가지고 있을 경우
     * @return 발행된 쿠폰들
     */
    @Override
    @Transactional
    public List<CouponResponseDto> issueCoupons(List<Long> userIds, Long couponTypeId) {
        List<CouponResponseDto> result = new ArrayList<>();
        validateId(couponTypeId);
        CouponType couponType = couponTypeRepository.findById(couponTypeId).orElseThrow(() -> new NotFoundException("쿠폰 정책(id:" + couponTypeId + ")이 존재하지 않습니다.1"));

        // 회원 존재 확인
        for (Long userId : userIds) {
            validateId(userId);
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));

            Optional<Coupon> unusedCoupon = couponRepository.findUnusedCouponByUserAndType(userId, couponTypeId);

            //회원이 해당 쿠폰타입의 UNUSED쿠폰을 가지고 있는지 확인
            if (unusedCoupon.isPresent()) {
                throw new AlreadyExistException("회원(id:" + userId + ")은 해당 쿠폰타입(id:" + couponTypeId + ")의 UNUSED 쿠폰을 가지고 있습니다.");
            }

            Coupon coupon = Coupon.builder()
                    .issueDate(LocalDateTime.now())
                    .deadline(calCouponDeadline(couponType))
                    .couponStatus(CouponStatus.UNUSED)
                    .couponType(couponType)
                    .user(user)
                    .build();

            Coupon savedCoupon = couponRepository.save(coupon);
            result.add(CouponMapper.toResponse(savedCoupon));

        }
        return result;

    }

    /**
     * 쿠폰을 만료시킨다.
     * @param userId
     * @param couponId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원,쿠폰이 존재하지 않을 경우
     * @return 만료된 쿠폰
     */
    @Override
    @Transactional
    public CouponResponseDto expireCoupon(Long userId, Long couponId) {
        validateId(userId);
        validateId(couponId);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));
        Coupon coupon = couponRepository.findByUserUserIdAndCouponId(userId, couponId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")은 쿠폰(id:" + couponId + ")을 가지고 있지 않습니다."));

        coupon.expire();

        return CouponMapper.toResponse(coupon);
    }

    /**
     * 쿠폰을 사용합니다.
     * @param userId
     * @param couponId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원,쿠폰이 존재하지 않을 경우
     * @return 사용한 쿠폰
     */
    @Override
    @Transactional
    public CouponResponseDto useCoupon(Long userId, Long couponId) {
        validateId(userId);
        validateId(couponId);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));
        Coupon coupon = couponRepository.findByUserUserIdAndCouponId(userId, couponId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")은 쿠폰(id:" + couponId + ")을 가지고 있지 않습니다."));
        if (coupon.getCouponStatus() != CouponStatus.UNUSED) {
            throw new AlreadyCouponUsed("회원(id:" + userId + ")의 쿠폰(id:" + couponId + ")은 이미 사용된 쿠폰입니다.");
        }
        coupon.use();

        return CouponMapper.toResponse(coupon);
    }

    /**
     * 쿠폰을 삭제합니다. (쿠폰 사용과 다름)
     * @param userId
     * @param couponId
     * @throws IllegalArgumentException id가 0이거나 null일 경우
     * @throws NotFoundException 회원,쿠폰이 존재하지 않을 경우
     */
    @Override
    @Transactional
    public void deleteCoupon(Long userId, Long couponId) {
        validateId(userId);
        validateId(couponId);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")이 존재하지 않습니다."));
        Coupon coupon = couponRepository.findByUserUserIdAndCouponId(userId, couponId).orElseThrow(() -> new NotFoundException("회원(id:" + userId + ")은 쿠폰(id:" + couponId + ")을 가지고 있지 않습니다."));

        couponRepository.delete(coupon);
    }

    /**
     * 주문 금액에 대해 적용된 쿠폰 할인 금액을 반환한다.
     * @param bookId
     * @param quantity
     * @param couponId
     * @throws IllegalArgumentException id,quantity가 0이거나 null일 경우
     * @throws NotFoundException 책,쿠폰이 존재하지 않을 경우
     * @throws InsufficientOrderAmountException 주문 금액(책 판매가 * 수량)이 쿠폰의 최소 주문 금액에 못 미칠 경우
     * @return 할인금액, 할인 전 금액, 할인 후 금액
     */
    @Override
    public DiscountAmountResponseDto calDiscountAmount(Long bookId, Integer quantity, Long couponId) {
        if (quantity < 1) {
            throw new IllegalArgumentException("책의 수량은 0보다 많아야합니다.");
        }
        validateId(bookId);
        validateId(couponId);
        Book book = bookRepository.findByBookId(bookId).orElseThrow(() -> new NotFoundException("책(id:" + bookId + ")이 존재하지 않습니다."));
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new NotFoundException("쿠폰(id:" + couponId + ")이 존재하지 않습니다."));
        List<List<CategoryResponseDto>> categoryList = bookRepository.getBookDetail(null, book.getBookId()).getCategoryList();


        // 쿠폰 적용 가능한지 확인
        if (coupon.getCouponType() instanceof BookCoupon bookCoupon) {
            if (!bookCoupon.getBook().getBookId().equals(bookId)) {
                throw new InapplicableCoupon("책 쿠폰(id:" + couponId + ")은 책(id:" + bookId + ")에 적용 불가능합니다.");
            }
        } else if (coupon.getCouponType() instanceof CategoryCoupon categoryCoupon) {
            boolean flag = true;
            for (List<CategoryResponseDto> categoryResponseDtos : categoryList) {
                for (CategoryResponseDto categoryResponseDto : categoryResponseDtos) {
                    if (categoryResponseDto.getCategoryId().equals(categoryCoupon.getCategory().getCategoryId())) {
                        flag = false;
                    }
                }
            }
            if (flag) {
                throw new InapplicableCoupon("카테고리 쿠폰(id:" + couponId + ")은 책(id:" + bookId + ")에 적용 불가능합니다.");
            }
        }

        //책 주문 금액 -> 책 판매가 * 개수
        BigDecimal bookOrderPrice = book.getSaleprice().multiply(new BigDecimal(quantity));

        // 쿠폰 정책
        CouponPolicy couponPolicy = coupon.getCouponType().getCouponPolicy();
        // 최소 주문 금액에 못미치면
        if (couponPolicy.getMinOrderAmount().compareTo(bookOrderPrice) > 0) {
            throw new InsufficientOrderAmountException("주문 금액(" + bookOrderPrice + ")이 쿠폰 최소 주문 금액(" + couponPolicy.getMinOrderAmount() + ")에 못미칩니다.");
        }


        if (couponPolicy.getDiscountType() == DisCountType.FIX) {
            return DiscountAmountResponseDto.builder()
                    .bookId(bookId)
                    .quantity(quantity)
                    .discountAmount(couponPolicy.getDiscountPrice())
                    .beforeCouponDiscount(bookOrderPrice)
                    .afterCouponDiscount(bookOrderPrice.subtract(couponPolicy.getDiscountPrice()))
                    .build();
        } else {
            // 소수점 첫번째 자리에서 반올림
            BigDecimal discountAmount = bookOrderPrice.multiply(couponPolicy.getDiscountRate()).divide(new BigDecimal(100),1, RoundingMode.HALF_UP);
            BigDecimal afterDiscount = bookOrderPrice.subtract(discountAmount);

            //만약 쿠폰을 적용한 할인금액이 최대 할인 금액보다 높다면
            if (couponPolicy.getMaxDiscountAmount().compareTo(discountAmount) < 0) {
                discountAmount = couponPolicy.getMaxDiscountAmount();
                afterDiscount = bookOrderPrice.subtract(discountAmount);
            }

            return DiscountAmountResponseDto.builder()
                    .bookId(bookId)
                    .quantity(quantity)
                    .discountAmount(discountAmount)
                    .beforeCouponDiscount(bookOrderPrice)
                    .afterCouponDiscount(afterDiscount)
                    .build();
        }

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

    /**
     * 쿠폰의 마감일을 계산해 반환한다.
     * @param couponType
     * @return
     */
    private LocalDateTime calCouponDeadline(CouponType couponType) {
        if (Objects.nonNull(couponType.getDeadline())) {
            return couponType.getDeadline();
        } else {
            return LocalDateTime.now().plusDays(couponType.getPeriod());
        }


    }
}
