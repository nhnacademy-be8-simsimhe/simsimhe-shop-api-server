package com.simsimbookstore.apiserver.coupons.coupon.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.bookcategory.entity.QBookCategory;
import com.simsimbookstore.apiserver.books.category.entity.QCategory;
import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.allcoupon.entity.QAllCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.QBookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.QCategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.entity.QCoupon;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.QCouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomCouponRepositoryImpl implements CustomCouponRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 유저가 가지고 있는 쿠폰 중에서 특정 책에 적용 가능한 쿠폰만 Page로 반환한다.
     * @param pageable
     * @param userId
     * @param bookId
     * @return 특정 책에 적용 가능한 쿠폰 페이지
     */
    @Override
    public List<Coupon> findEligibleCouponToBook(Long userId, Long bookId) {
        QCoupon coupon = QCoupon.coupon;
        QCouponType couponType = QCouponType.couponType;
        QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;
        QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
        QAllCoupon allCoupon = QAllCoupon.allCoupon;
        QBookCategory bookCategory = QBookCategory.bookCategory;

        JPAQuery<Coupon> query = jpaQueryFactory.selectFrom(coupon)
                .join(coupon.couponType, couponType)
                .leftJoin(categoryCoupon).on(couponType.couponTypeId.eq(categoryCoupon.couponTypeId))
                .leftJoin(bookCoupon).on(couponType.couponTypeId.eq(bookCoupon.couponTypeId))
                .leftJoin(allCoupon).on(couponType.couponTypeId.eq(allCoupon.couponTypeId))
                .where(coupon.user.userId.eq(userId)
                        .and(coupon.couponStatus.eq(CouponStatus.UNUSED))
                        .and(
                                couponType.instanceOf(CategoryCoupon.class)
                                        .or(
                                                couponType.instanceOf(BookCoupon.class)
                                                        .and(bookCoupon.book.bookId.eq(bookId))
                                        )
                                        .or(
                                                couponType.instanceOf(AllCoupon.class)
                                        )
                        )
                )
                .orderBy(coupon.issueDate.asc());


        return query.fetch();
    }

    /**
     * 유저가 가지고 있는 쿠폰 중에서 특정 쿠폰 타입이고 아직 사용되지 않은(UNUSED)쿠폰을 반환한다
     * 아직 사용하지 않은 특정 쿠폰 타입인 쿠폰은 사용자마다 1개씩만 가지고 있을 수 있음.
     * @param userId
     * @param couponTypeId
     * @return 사용되지 않은 쿠폰
     */
    @Override
    public Optional<Coupon> findUnusedCouponByUserAndType(Long userId, Long couponTypeId) {
        QCoupon coupon = QCoupon.coupon;
        return Optional.ofNullable(jpaQueryFactory.selectFrom(coupon)
                .where(
                        coupon.user.userId.eq(userId),
                        coupon.couponType.couponTypeId.eq(couponTypeId),
                        coupon.couponStatus.eq(CouponStatus.UNUSED)
                ).fetchOne());
    }
}
