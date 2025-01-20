package com.simsimbookstore.apiserver.reviews.review.controller;

import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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


    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable Long bookId, @RequestParam Long userId, @RequestParam(value = "images", required = false) List<MultipartFile> files, @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {

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
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long bookId,
                                                           @PathVariable Long reviewId) {
        ReviewResponseDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }


    @GetMapping
    public ResponseEntity<Page<ReviewLikeCountDTO>> getAllReviewsOrderBySort(@PathVariable Long bookId,
                                                                             @RequestParam Long userId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "recommend") String sort) {

        var reviews = reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort);

        return ResponseEntity.ok(reviews);
    }


    @PostMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long bookId,
                                                          @PathVariable Long reviewId,
                                                          @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        Review updatedReview = reviewService.updateReview(reviewRequestDTO, reviewId);

        return ResponseEntity.ok(new ReviewResponseDTO(updatedReview));
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }


}
