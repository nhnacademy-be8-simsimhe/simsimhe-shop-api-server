package com.simsimbookstore.apiserver.reviews.reviewimage.entity;


import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_image_paths")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ReviewImagePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_path_id")
    private Long reviewImagePathId;

    @Column(name = "image_name", nullable = false, length = 100)
    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

}
