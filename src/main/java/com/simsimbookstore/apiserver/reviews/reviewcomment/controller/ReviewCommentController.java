package com.simsimbookstore.apiserver.reviews.reviewcomment.controller;


import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import com.simsimbookstore.apiserver.reviews.reviewcomment.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shop/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService reviewCommentService;

    /**
     * 댓글 생성
     */
    @PostMapping
    public ResponseEntity<ReviewCommentResponseDTO> createReviewComment(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @RequestBody ReviewCommentRequestDTO requestDTO) {

        ReviewCommentResponseDTO reviewComment = reviewCommentService.createReviewComment(requestDTO, reviewId, userId);
        log.info("reviewComment : {}", reviewComment.getContent());
        return ResponseEntity.ok(reviewComment);
    }


    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ReviewComment> updateReviewComment(
            @PathVariable Long reviewId,
            @PathVariable Long commentId,
            @RequestBody ReviewCommentRequestDTO requestDTO) {

        ReviewComment updatedComment = reviewCommentService.updateReviewComment(reviewId, commentId, requestDTO);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * 댓글 조회 (단일)
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ReviewComment> getReviewCommentById(
            @PathVariable Long reviewId, @PathVariable Long commentId) {
        ReviewComment reviewComment = reviewCommentService.getReviewCommentById(commentId);
        return ResponseEntity.ok(reviewComment);
    }


    /**
     * 전체 댓글 조회 (해당 리뷰에 대한)
     */
    @GetMapping
    public ResponseEntity<Page<ReviewCommentResponseDTO>> getReviewComments(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewCommentResponseDTO> reviewComments = reviewCommentService.getReviewComments(reviewId, page, size);
        return ResponseEntity.ok(reviewComments);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteReviewComment(@PathVariable Long reviewId, @PathVariable Long commentId) {
        reviewCommentService.deleteReviewComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
