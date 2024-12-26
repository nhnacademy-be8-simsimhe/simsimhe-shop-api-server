package com.simsimbookstore.apiserver.coupons.coupon.entity;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "coupon_status")
    private CouponStatus couponStatus;

    @Column(name = "use_date")
    private LocalDateTime useDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id", nullable = false)
    private CouponType couponType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void use() {
        couponStatus = CouponStatus.USED;
        useDate = LocalDateTime.now();
    }

    public void expire() {
        couponStatus = CouponStatus.EXPIRED;
    }
}
