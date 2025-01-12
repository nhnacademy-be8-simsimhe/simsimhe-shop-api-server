package com.simsimbookstore.apiserver.reviews.reviewimage.controller;


import com.simsimbookstore.apiserver.reviews.reviewimage.dto.ReviewImgPathResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.service.ReviewImagePathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shop/reviews/{reviewId}/images")
@RequiredArgsConstructor
public class ReviewImageController {

    private final ReviewImagePathService reviewImagePathService;

    @PostMapping
    public ResponseEntity<?> addReviewImages(@PathVariable Long reviewId, @RequestBody List<String> imageName) {
        log.info("imageName : {}", imageName);
        List<ReviewImgPathResponseDTO> savedImage = reviewImagePathService.createReviewImage(reviewId, imageName);
        return ResponseEntity.ok(savedImage);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        reviewImagePathService.deleteReviewImage(imageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewImagePath>> getImagesByReview(@PathVariable Long reviewId) {
        List<ReviewImagePath> images = reviewImagePathService.getImagesByReviewId(reviewId);
        return ResponseEntity.ok(images);
    }

}
