package com.simsimbookstore.apiserver.reviews.review.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookRepository bookRepository;


    @Test
    @DisplayName("리뷰 생성 서비스 성공")
    void createReview_success() {
        Long bookId = 1L;
        Long userId = 2L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");

        User user = User.builder().userId(userId).build();
        Book book = Book.builder().bookId(bookId).build();


        Review review = Review.builder()
                .score(dto.getScore())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .book(book)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(dto, bookId, userId);

        assertNotNull(result);
        assertEquals(dto.getScore(), result.getScore());
        assertEquals(dto.getTitle(), result.getTitle());
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 서비스 성공")
    void updateReview_success() {
        Long reviewId = 1L;
        ReviewRequestDTO dto = new ReviewRequestDTO(4, "Updated Title", "Updated Content");

        Review existingReview = Review.builder()
                .reviewId(reviewId)
                .score(5)
                .title("Old Title")
                .content("Old Content")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        Review result = reviewService.updateReview(dto, reviewId);

        assertNotNull(result);
        assertEquals(dto.getScore(), result.getScore());
        assertEquals(dto.getTitle(), result.getTitle());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    @DisplayName("해당 도서에 대한 전체 리뷰 조회 성공")
    void getReviewsByBook_success() {
        Long bookId = 1L;
        int page = 0;
        int size = 10;

        Book book = Book.builder().bookId(bookId).build();


        Review review1 = Review.builder().reviewId(1L).score(5).title("Title1").content("Content1").build();
        Review review2 = Review.builder().reviewId(2L).score(4).title("Title2").content("Content2").build();

        Page<Review> reviewPage = new PageImpl<>(List.of(review1, review2), PageRequest.of(page, size), 2);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBook(book, PageRequest.of(page, size))).thenReturn(reviewPage);

        Page<Review> result = reviewService.getReviewsByBook(bookId, page, size);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).findAllByBook(book, PageRequest.of(page, size));
    }

    @Test
    @DisplayName("해당 리뷰 가져오기")
    void getReviewById() {
        Long reviewId = 1L;

        Review review = Review.builder().reviewId(reviewId).score(5).title("Title").content("Content").build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewResponseDTO result = reviewService.getReviewById(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("리뷰 삭제 서비스 성공")
    void deleteReview_success() {
        Long reviewId = 1L;

        Review existingReview = Review.builder().reviewId(reviewId).build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));

        reviewService.deleteReview(reviewId);

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).delete(existingReview);

    }
}