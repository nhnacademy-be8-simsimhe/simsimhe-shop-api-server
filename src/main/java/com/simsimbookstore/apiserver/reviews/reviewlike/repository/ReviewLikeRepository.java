package com.simsimbookstore.apiserver.reviews.reviewlike.repository;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import com.simsimbookstore.apiserver.users.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    int countAllByReview(Review review);
    int countAllByUser(User user);

    List<ReviewLike> findAllByReview(Review review);
    List<ReviewLike> findAllByUser(User user);


}
