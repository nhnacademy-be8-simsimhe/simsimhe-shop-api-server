package com.simsimbookstore.apiserver.reviews.reviewlike.entity;


import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
//사용자는 리뷰에대한 공감을 한번만 누를수있음 유니크 제약조건을 설정해도 로직에서 중복 삽입 방지하는 코드추가
@Table(name = "review_likes",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "review_id"})
       })
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ReviewLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long reviewLikeId;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
