package com.simsimbookstore.apiserver.reviews.reviewimage.service;

import com.simsimbookstore.apiserver.reviews.reviewimage.dto.ReviewImgPathResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;

import java.util.List;

public interface ReviewImagePathService {

    List<ReviewImgPathResponseDTO> createReviewImage(Long reviewId, List<String> imageName);
    void deleteReviewImage(Long imageId);
    List<ReviewImagePath> getImagesByReviewId(Long reviewId);
}
