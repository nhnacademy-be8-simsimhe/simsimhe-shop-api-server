package com.simsimbookstore.apiserver.reviews.reviewcomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import com.simsimbookstore.apiserver.reviews.reviewcomment.service.ReviewCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ReviewCommentController.class)
class ReviewCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewCommentService reviewCommentService;

    @Test
    @DisplayName("댓글 생성 테스트")
    void createReviewComment() throws Exception {
//        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("This is a comment");
//        ReviewComment reviewComment = ReviewComment.builder()
//                .reviewCommentId(1L)
//                .content("This is a comment")
//                .created_at(LocalDateTime.now())
//                .build();
//
////        ReviewCommentResponseDTO reviewCommentResponseDTO = new Rev
//
//        when(reviewCommentService.createReviewComment(any(ReviewCommentRequestDTO.class), anyLong(), anyLong()))
//                .thenReturn(reviewComment);
//
//        mockMvc.perform(post("/api/reviews/{reviewId}/comments", 1L)
//                        .param("userId", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.reviewCommentId").value(1L))
//                .andExpect(jsonPath("$.content").value("This is a comment"));
//
//        verify(reviewCommentService).createReviewComment(any(ReviewCommentRequestDTO.class), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateReviewComment() throws Exception {
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Updated comment");
        ReviewComment updatedComment = ReviewComment.builder()
                .reviewCommentId(1L)
                .content("Updated comment")
                .update_at(LocalDateTime.now())
                .build();

        when(reviewCommentService.updateReviewComment(any(ReviewCommentRequestDTO.class), anyLong()))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/reviews/{reviewId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCommentId").value(1L))
                .andExpect(jsonPath("$.content").value("Updated comment"));

        verify(reviewCommentService).updateReviewComment(any(ReviewCommentRequestDTO.class), eq(1L));
    }

    @Test
    @DisplayName("댓글 단일 조회 테스트")
    void getReviewCommentById() throws Exception {
        ReviewComment reviewComment = ReviewComment.builder()
                .reviewCommentId(1L)
                .content("This is a comment")
                .created_at(LocalDateTime.now())
                .build();

        when(reviewCommentService.getReviewCommentById(anyLong())).thenReturn(reviewComment);

        mockMvc.perform(get("/api/reviews/{reviewId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCommentId").value(1L))
                .andExpect(jsonPath("$.content").value("This is a comment"));

        verify(reviewCommentService).getReviewCommentById(eq(1L));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteReviewComment() throws Exception {
        Mockito.doNothing().when(reviewCommentService).deleteReviewComment(anyLong());

        mockMvc.perform(delete("/api/reviews/{reviewId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reviewCommentService).deleteReviewComment(eq(1L));
    }

}