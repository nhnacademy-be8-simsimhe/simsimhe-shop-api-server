package com.simsimbookstore.apiserver.reviews.reviewlike.controller;


import com.simsimbookstore.apiserver.reviews.reviewlike.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}/likes")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping
    public ResponseEntity<Void> createLike(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewLikeService.createReviewLike(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewLikeService.deleteReviewLike(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long reviewId) {
        long count = reviewLikeService.getReviewLikeCount(reviewId);
        return ResponseEntity.ok(count);
    }

}
