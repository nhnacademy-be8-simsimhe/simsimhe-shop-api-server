package com.simsimbookstore.apiserver.reviews.reviewimage.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.dto.ReviewImgPathResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.service.ReviewImagePathService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ReviewImagePathServiceImpl implements ReviewImagePathService {


    private final ReviewImagePathRepository reviewImagePathRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 이미지 추가
     */


    @Transactional
    @Override
    public List<ReviewImgPathResponseDTO> createReviewImage(Long reviewId, List<String> imageName) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        List<ReviewImgPathResponseDTO> imagePaths = new ArrayList<>();

        for (String path : imageName) {
            ReviewImagePath reviewImagePath = ReviewImagePath.builder()
                    .imageName(path)
                    .review(review)
                    .build();

            reviewImagePathRepository.save(reviewImagePath);

            ReviewImgPathResponseDTO dto = convertToDTO(reviewImagePath);
            imagePaths.add(dto);
        }


        return imagePaths;
    }

    /**
     * 리뷰 이미지 삭제
     */
    @Transactional
    public void deleteReviewImage(Long imageId) {
        ReviewImagePath reviewImagePath = reviewImagePathRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이미지입니다."));

        reviewImagePathRepository.delete(reviewImagePath);
    }

    /**
     * 특정 리뷰에 대한 이미지 목록 조회
     */
    public List<ReviewImagePath> getImagesByReviewId(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        return reviewImagePathRepository.findByReview(review);
    }

    private ReviewImgPathResponseDTO convertToDTO(ReviewImagePath reviewImagePath) {

        return new ReviewImgPathResponseDTO(reviewImagePath.getReviewImagePathId(), reviewImagePath.getImageName(), reviewImagePath.getReview().getReviewId());
    }
}
