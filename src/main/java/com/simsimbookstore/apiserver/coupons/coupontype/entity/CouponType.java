package com.simsimbookstore.apiserver.coupons.coupontype.entity;


import jakarta.persistence.*;
import lombok.*;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import java.time.LocalDateTime;


@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "coupon_type")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_types")
public class CouponType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_type_id")
    private Long couponTypeId;

    @Column(name = "coupon_type_name", length = 40, nullable = false)
    private String couponTypeName;

    @Column(name = "period")
    private int period;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "coupon_stacking", nullable = false)
    private boolean stacking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;
}
