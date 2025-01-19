package com.simsimbookstore.apiserver.reviews.reviewimage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.reviews.reviewimage.dto.ReviewImgPathResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.service.ReviewImagePathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewImageControllerTest {

    private MockMvc mockMvc;
    private ReviewImagePathService reviewImagePathService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        reviewImagePathService = mock(ReviewImagePathService.class);
        ReviewImageController reviewImageController = new ReviewImageController(reviewImagePathService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewImageController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("리뷰 이미지 추가 테스트")
    void addImage() throws Exception {
        Long reviewId = 1L;

        List<String> imagePaths = List.of(
                "/images/review1/image1.jpg",
                "/images/review1/image2.jpg",
                "/images/review2/image1.jpg"
        );

        List<ReviewImgPathResponseDTO> imgPathResponseDTOList = List.of(
                new ReviewImgPathResponseDTO(1L, "/images/review1/image1.jpg", reviewId),
                new ReviewImgPathResponseDTO(2L, "/images/review1/image2.jpg", reviewId),
                new ReviewImgPathResponseDTO(3L, "/images/review1/image3.jpg", reviewId)

        );

        when(reviewImagePathService.createReviewImage(eq(reviewId), eq(imagePaths)))
                .thenReturn(imgPathResponseDTOList);

        mockMvc.perform(post("/api/shop/reviews/{reviewId}/images", reviewId)
                        .content(objectMapper.writeValueAsString(imagePaths))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewImagePathId").value(imgPathResponseDTOList.get(0).getReviewImagePathId()))
                .andExpect(jsonPath("$[0].imageName").value(imgPathResponseDTOList.get(0).getImageName()));

        verify(reviewImagePathService, times(1)).createReviewImage(eq(reviewId), eq(imagePaths));
    }

    @Test
    @DisplayName("리뷰 이미지 삭제 테스트")
    void deleteImage() throws Exception {
        Long imageId = 1L;

        doNothing().when(reviewImagePathService).deleteReviewImage(eq(imageId));

        mockMvc.perform(delete("/api/shop/reviews/{reviewId}/images/{imageId}", 1L, imageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reviewImagePathService, times(1)).deleteReviewImage(eq(imageId));
    }

    @Test
    @DisplayName("리뷰에 연결된 이미지 조회 테스트")
    void getImagesByReview() throws Exception {
        Long reviewId = 1L;

        List<ReviewImagePath> mockImages = List.of(
                ReviewImagePath.builder().reviewImagePathId(1L).imageName("/images/review1/image1.jpg").build(),
                ReviewImagePath.builder().reviewImagePathId(2L).imageName("/images/review1/image2.jpg").build()
        );

        when(reviewImagePathService.getImagesByReviewId(eq(reviewId))).thenReturn(mockImages);

        mockMvc.perform(get("/api/shop/reviews/{reviewId}/images", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(mockImages.size()))
                .andExpect(jsonPath("$[0].reviewImagePathId").value(mockImages.get(0).getReviewImagePathId()))
                .andExpect(jsonPath("$[0].imageName").value(mockImages.get(0).getImageName()))
                .andExpect(jsonPath("$[1].reviewImagePathId").value(mockImages.get(1).getReviewImagePathId()))
                .andExpect(jsonPath("$[1].imageName").value(mockImages.get(1).getImageName()));

        verify(reviewImagePathService, times(1)).getImagesByReviewId(eq(reviewId));
    }

}