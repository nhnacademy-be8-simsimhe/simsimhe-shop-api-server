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

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status")
    private CouponStatus couponStatus;

    @Column(name = "coupon_status")
    private LocalDateTime userDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id")
    private CouponType couponType;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    public enum CouponStatus {
        USED, EXPIRED, UNUSED
    }
    @Builder
    public Coupon(Long couponId, LocalDateTime issueDate, LocalDateTime deadline, CouponStatus couponStatus, LocalDateTime userDate, CouponType couponType
//            , User user
            ) {
        this.couponId = couponId;
        this.issueDate = issueDate;
        this.deadline = deadline;
        this.couponStatus = couponStatus;
        this.userDate = userDate;
        this.couponType = couponType;
//        this.user = user;
    }
}
