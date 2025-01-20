package com.simsimbookstore.apiserver.orders.coupondiscount.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.book.entity.QBook;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.entity.QCoupon;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.QCouponType;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.QCouponDiscount;
import com.simsimbookstore.apiserver.orders.order.entity.QOrder;
import com.simsimbookstore.apiserver.orders.orderbook.entity.QOrderBook;
import com.simsimbookstore.apiserver.users.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomCouponDiscountRepositoryImpl implements CustomCouponDiscountRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<CouponDiscount> getUserCouponDiscount(Long userId, Pageable pageable) {
        QUser user = QUser.user;
        QOrder order = QOrder.order;
        QOrderBook orderBook = QOrderBook.orderBook;
        QCouponDiscount couponDiscount = QCouponDiscount.couponDiscount;
        QBook book = QBook.book;
        QCoupon coupon = QCoupon.coupon;
        QCouponType couponType = QCouponType.couponType;

        List<CouponDiscount> content = jpaQueryFactory.selectFrom(couponDiscount)
                .join(couponDiscount.coupon, coupon)
                .join(couponDiscount.orderBook, orderBook)
                .join(orderBook.order, order)
                .join(order.user, user)
                .where(user.userId.eq(userId))
                .where(coupon.couponStatus.eq(CouponStatus.USED))
                .where(couponDiscount.usage.eq(true))
                .orderBy(coupon.useDate.desc())
                .offset(pageable.getOffset()) // 시작 위치 지정
                .limit(pageable.getPageSize())
                .fetch();
        // null 처리: total이 null인 경우 0으로 설정
        long total = Objects.requireNonNullElse(jpaQueryFactory.select(couponDiscount.count())
                .from(couponDiscount)
                .join(couponDiscount.coupon, coupon)
                .join(couponDiscount.orderBook, orderBook)
                .join(orderBook.order, order)
                .join(order.user, user)
                .where(user.userId.eq(userId))
                .where(couponDiscount.usage.eq(true))
                .where(coupon.couponStatus.eq(CouponStatus.USED))
                .fetchOne(), 0L);

        return new PageImpl<>(content, pageable, total);
    }
}
