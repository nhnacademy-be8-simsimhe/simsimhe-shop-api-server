package com.simsimbookstore.apiserver.reviews.reviewlike.repository;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    //특정 유저가 좋아요한 리뷰의 개수 가져오기
    long countByUser(User user);

    // 특정 리뷰에 대한 좋아요 개수 가져오기
    long countByReview(Review review);


    List<ReviewLike> findAllByReview(Review review);
    List<ReviewLike> findAllByUser(User user);
    // 특정 사용자가 특정 리뷰를 좋아요했는지 확인
    Optional<ReviewLike> findByUserAndReview(User user, Review review);


}
