package com.simsimbookstore.apiserver.reviews.reviewcomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentRequestDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.dto.ReviewCommentResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewcomment.entity.ReviewComment;
import com.simsimbookstore.apiserver.reviews.reviewcomment.service.ReviewCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ReviewCommentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReviewCommentService reviewCommentService;

    @BeforeEach
    void setup() {
        reviewCommentService = mock(ReviewCommentService.class);
        ReviewCommentController reviewCommentController = new ReviewCommentController(reviewCommentService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewCommentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createReviewCommentTest() throws Exception {
        Long reviewId = 1L;
        Long userId = 2L;
        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("This is a comment");
        ReviewCommentResponseDTO responseDTO = new ReviewCommentResponseDTO(1L, "This is a comment", LocalDateTime.now(), LocalDateTime.now(), "user1", 1L);

        when(reviewCommentService.createReviewComment(any(ReviewCommentRequestDTO.class), eq(reviewId), eq(userId)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/shop/reviews/{reviewId}/comments", reviewId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("reviewCommentId").value(1L))
                .andExpect(jsonPath("content").value("This is a comment"))
                .andExpect(jsonPath("userName").value("user1"));

        verify(reviewCommentService).createReviewComment(any(ReviewCommentRequestDTO.class), eq(reviewId), eq(userId));
    }

//    @Test
//    @DisplayName("댓글 수정 테스트")
//    void updateReviewCommentTest() throws Exception {
//        Long reviewId = 1L;
//        Long commentId = 1L;
//        ReviewCommentRequestDTO requestDTO = new ReviewCommentRequestDTO("Updated comment");
//        ReviewComment updatedComment = ReviewComment.builder()
//                .reviewCommentId(commentId)
//                .content("Updated comment")
//                .update_at(LocalDateTime.now())
//                .build();
//
//        when(reviewCommentService.updateReviewComment(eq(reviewId), eq(commentId), any(ReviewCommentRequestDTO.class)))
//                .thenReturn(updatedComment);
//
//        mockMvc.perform(put("/api/shop/reviews/{reviewId}/comments/{commentId}", reviewId, commentId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.reviewCommentId").value(commentId))
//                .andExpect(jsonPath("$.content").value("Updated comment"));
//
//        verify(reviewCommentService).updateReviewComment(eq(reviewId), eq(commentId), any(ReviewCommentRequestDTO.class));
//    }

    @Test
    @DisplayName("댓글 단일 조회 테스트")
    void getReviewCommentByIdTest() throws Exception {
        Long reviewId = 1L;
        Long commentId = 1L;
        ReviewComment reviewComment = ReviewComment.builder()
                .reviewCommentId(commentId)
                .content("This is a comment")
                .created_at(LocalDateTime.now())
                .build();

        when(reviewCommentService.getReviewCommentById(eq(commentId))).thenReturn(reviewComment);

        mockMvc.perform(get("/api/shop/reviews/{reviewId}/comments/{commentId}", reviewId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.reviewCommentId").value(commentId))
                .andExpect(jsonPath("$.content").value("This is a comment"));

        verify(reviewCommentService).getReviewCommentById(eq(commentId));
    }

    @Test
    @DisplayName("댓글 전체 조회 테스트")
    void getReviewCommentsTest() throws Exception {
        Long reviewId = 1L;

        int page = 0;
        int size = 10;
        var comments = List.of(
                new ReviewCommentResponseDTO(1L, "Comment 1", LocalDateTime.now(), LocalDateTime.now(), "user1", 1L),
                new ReviewCommentResponseDTO(2L, "Comment 2", LocalDateTime.now(), LocalDateTime.now(), "user2", 1L)
        );

        var pageable = PageRequest.of(page, size);
        var response = new PageImpl<>(comments, pageable, comments.size());

        when(reviewCommentService.getReviewComments(eq(reviewId), eq(0), eq(10)))
                .thenReturn(response);

        mockMvc.perform(get("/api/shop/reviews/{reviewId}/comments", reviewId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reviewCommentId").value(1L))
                .andExpect(jsonPath("$.content[0].content").value("Comment 1"))
                .andExpect(jsonPath("$.content[1].reviewCommentId").value(2L))
                .andExpect(jsonPath("$.content[1].content").value("Comment 2"));

        verify(reviewCommentService).getReviewComments(eq(reviewId), eq(0), eq(10));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteReviewCommentTest() throws Exception {
        Long reviewId = 1L;
        Long commentId = 1L;

        doNothing().when(reviewCommentService).deleteReviewComment(eq(commentId));

        mockMvc.perform(delete("/api/shop/reviews/{reviewId}/comments/{commentId}", reviewId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reviewCommentService).deleteReviewComment(eq(commentId));
    }
}
