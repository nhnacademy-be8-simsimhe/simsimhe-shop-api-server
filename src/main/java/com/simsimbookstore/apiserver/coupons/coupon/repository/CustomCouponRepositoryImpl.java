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

    @Override
    public Page<Coupon> findEligibleCouponToBook(Pageable pageable, Long userId, Long bookId) {
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
                .leftJoin(bookCategory).on(bookCategory.book.bookId.eq(bookId))
                .where(coupon.user.userId.eq(userId)
                        .and(
                                couponType.instanceOf(CategoryCoupon.class)
                                        .and(categoryCoupon.category.categoryId.eq(bookCategory.catagory.categoryId))
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

        // 페이징 적용: limit과 offset 설정
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Coupon> coupons = query.fetch();

        // 전체 카운트 조회
        JPAQuery<Long> countQuery = jpaQueryFactory.select(coupon.count())
                .from(coupon)
                .join(coupon.couponType, couponType)
                .leftJoin(categoryCoupon).on(couponType.couponTypeId.eq(categoryCoupon.couponTypeId))
                .leftJoin(bookCoupon).on(couponType.couponTypeId.eq(bookCoupon.couponTypeId))
                .leftJoin(allCoupon).on(couponType.couponTypeId.eq(allCoupon.couponTypeId))
                .leftJoin(bookCategory).on(bookCategory.book.bookId.eq(bookId))
                .where(
                        coupon.user.userId.eq(userId)
                                .and(
                                        couponType.instanceOf(CategoryCoupon.class)
                                                .and(categoryCoupon.category.categoryId.eq(bookCategory.catagory.categoryId))
                                                .or(
                                                        couponType.instanceOf(BookCoupon.class)
                                                                .and(bookCoupon.book.bookId.eq(bookId))
                                                )
                                                .or(
                                                        couponType.instanceOf(AllCoupon.class)
                                                )
                                )
                );
        Long total = countQuery.fetchOne();
        return new PageImpl<>(coupons, pageable, total);
    }

    @Override
    public List<Coupon> findUnusedCouponByUserAndType(Long userId, Long couponTypeId) {
        QCoupon coupon = QCoupon.coupon;
        return jpaQueryFactory.selectFrom(coupon)
                .where(
                        coupon.user.userId.eq(userId),
                        coupon.couponType.couponTypeId.eq(couponTypeId),
                        coupon.couponStatus.eq(CouponStatus.UNUSED)
                ).fetch();
    }
}
