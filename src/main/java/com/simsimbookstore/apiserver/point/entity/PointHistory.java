package com.simsimbookstore.apiserver.point.entity;

import com.simsimbookstore.apiserver.users.user.entity.User;
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
    @Column(name = "point_type", nullable = false)
    private PointType pointType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_policy_id", nullable = false)
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
