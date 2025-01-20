package com.simsimbookstore.apiserver.reviews.review.controller;

import com.simsimbookstore.apiserver.reviews.review.dto.UserAvailableReviewsDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.UserReviewsDTO;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/users/{userId}/reviews")
@RequiredArgsConstructor
public class UserReviewController {


    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<UserReviewsDTO>> getUserReviews(@PathVariable Long userId, @RequestParam int page, @RequestParam int size) {
        Page<UserReviewsDTO> userReviews = reviewService.getUserReviews(userId, page, size);
        return ResponseEntity.ok(userReviews);
    }


    @GetMapping("/available")
    public ResponseEntity<Page<UserAvailableReviewsDTO>> getEligibleBooksForReview(@PathVariable Long userId, @RequestParam int page, @RequestParam int size) {
        Page<UserAvailableReviewsDTO> userReviews = reviewService.getAvailableReviews(userId, page, size);
        return ResponseEntity.ok(userReviews);
    }

}
