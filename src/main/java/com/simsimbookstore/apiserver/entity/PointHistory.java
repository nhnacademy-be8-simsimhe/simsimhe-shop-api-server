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
@Table(name = "point_histories")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long pointHistoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type")
    private PointType pointType;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "created_at")
    private LocalDateTime created_at;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_policy_id")
    private PointPolicy pointPolicy;

    @Builder
    public PointHistory(Long pointHistoryId, PointType pointType, Integer amount, LocalDateTime created_at,
//                        User user,
                        PointPolicy pointPolicy) {
        this.pointHistoryId = pointHistoryId;
        this.pointType = pointType;
        this.amount = amount;
        this.created_at = created_at;
//        this.user = user;
        this.pointPolicy = pointPolicy;
    }

    public enum PointType {
        EARN, DEDUCT
    }
}
