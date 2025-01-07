package com.simsimbookstore.apiserver.reviews.review.controller;


import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import com.simsimbookstore.apiserver.reviews.reviewimage.service.ReviewImagePathService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shop/books/{bookId}/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewImagePathService reviewImagePathService;


    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable Long bookId, @RequestParam Long userId, @RequestParam(value = "images", required = false) List<MultipartFile> files,  @Valid @RequestBody ReviewRequestDTO reviewRequestDTO){
        Review review = reviewService.createReview(reviewRequestDTO, bookId, userId);
        ReviewResponseDTO response = new ReviewResponseDTO(
                review.getReviewId(),
                review.getTitle(),
                review.getContent(),
                review.getScore(),
                LocalDateTime.now(),
                bookId,
                userId
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long bookId,
            @PathVariable Long reviewId) {
        ReviewResponseDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }


    @GetMapping
    public ResponseEntity<?> getAllReviews(@PathVariable Long bookId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        var reviews = reviewService.getAllReviews(bookId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/score")
    public ResponseEntity<?> getAllReviewsOrderByScore(@PathVariable Long bookId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        var reviews = reviewService.getReviewsByBookOrderByScore(bookId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/like")
    public ResponseEntity<?> getAllReviewsOrderByLike(@PathVariable Long bookId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        var reviews = reviewService.getReviewsByBookOrderByLike(bookId, page, size);
        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/recent")
    public ResponseEntity<?> getAllReviewsOrderByRecent(@PathVariable Long bookId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("bookId : {}", bookId);
        var reviews = reviewService.getReviewsByBookOrderByRecent(bookId, page, size);

        return ResponseEntity.ok(reviews);
    }



    @PostMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long bookId,
                                          @PathVariable Long reviewId,
                                          @Valid @RequestBody ReviewRequestDTO reviewRequestDTO){

        Review updatedReview = reviewService.updateReview(reviewRequestDTO,reviewId);

        return ResponseEntity.ok(new ReviewResponseDTO(updatedReview));
    }



    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId){

        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }






}
