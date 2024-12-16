package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name = "issue_date",nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status", nullable = false)
    private CouponStatus couponStatus;

    @Column(name = "use_date")
    private LocalDateTime useDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id", nullable = false)
    private CouponType couponType;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id",nullable = false)
//    private User user;

    public enum CouponStatus {
        USED, EXPIRED, UNUSED
    }
    @Builder
    public Coupon(Long couponId, LocalDateTime issueDate, LocalDateTime deadline, CouponStatus couponStatus, LocalDateTime useDate, CouponType couponType
//            , User user
            ) {
        this.couponId = couponId;
        this.issueDate = issueDate;
        this.deadline = deadline;
        this.couponStatus = couponStatus;
        this.useDate = useDate;
        this.couponType = couponType;
//        this.user = user;
    }
}
