package com.simsimbookstore.apiserver.reviews.reviewimage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.controller.ReviewController;
import com.simsimbookstore.apiserver.reviews.reviewcomment.controller.ReviewCommentController;
import com.simsimbookstore.apiserver.reviews.reviewcomment.service.ReviewCommentService;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.service.ReviewImagePathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewImageControllerTest {

    private MockMvc mockMvc;
    private ReviewImagePathService reviewImagePathService;

    @BeforeEach
    void setup() {
        reviewImagePathService = mock(ReviewImagePathService.class);
        ReviewImageController reviewImageController = new ReviewImageController(reviewImagePathService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewImageController).build();
    }

//    @Test
//    @DisplayName("리뷰 이미지 추가 테스트")
//    void addImage() throws Exception {
//        Long reviewId = 1L;
//        String imageName = "/images/review1/image1.jpg";
//
//        ReviewImagePath mockImage = ReviewImagePath.builder()
//                .reviewImagePathId(1L)
//                .imageName(imageName)
//                .build();
//
//        when(reviewImagePathService.createReviewImage(eq(reviewId), eq(imageName)))
//                .thenReturn(mockImage);
//
//        mockMvc.perform(post("/api/reviews/{reviewId}/images", reviewId)
//                        .param("imageName", imageName)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.reviewImagePathId").value(mockImage.getReviewImagePathId()))
//                .andExpect(jsonPath("$.imageName").value(mockImage.getImageName()));
//
//        verify(reviewImagePathService, times(1)).createReviewImage(eq(reviewId), eq(imageName));
//    }

    @Test
    @DisplayName("리뷰 이미지 삭제 테스트")
    void deleteImage() throws Exception {
        Long imageId = 1L;

        doNothing().when(reviewImagePathService).deleteReviewImage(eq(imageId));

        mockMvc.perform(delete("/api/reviews/{reviewId}/images/{imageId}", 1L, imageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reviewImagePathService, times(1)).deleteReviewImage(eq(imageId));
    }

//    @Test
//    @DisplayName("리뷰에 연결된 이미지 조회 테스트")
//    void getImagesByReview() throws Exception {
//        Long reviewId = 1L;
//
//        List<ReviewImagePath> mockImages = List.of(
//                ReviewImagePath.builder().reviewImagePathId(1L).imageName("/images/review1/image1.jpg").build(),
//                ReviewImagePath.builder().reviewImagePathId(2L).imageName("/images/review1/image2.jpg").build()
//        );
//
//        when(reviewImagePathService.getImagesByReviewId(eq(reviewId))).thenReturn(mockImages);
//
//        mockMvc.perform(get("/api/reviews/{reviewId}/images", reviewId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(mockImages.size()))
//                .andExpect(jsonPath("$[0].reviewImagePathId").value(mockImages.get(0).getReviewImagePathId()))
//                .andExpect(jsonPath("$[0].imageName").value(mockImages.get(0).getImageName()))
//                .andExpect(jsonPath("$[1].reviewImagePathId").value(mockImages.get(1).getReviewImagePathId()))
//                .andExpect(jsonPath("$[1].imageName").value(mockImages.get(1).getImageName()));
//
//        verify(reviewImagePathService, times(1)).getImagesByReviewId(eq(reviewId));
//    }

}