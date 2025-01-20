package com.simsimbookstore.apiserver.reviews.review.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.dto.*;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.exception.NotCreateReviewException;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
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
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    ReviewImagePathRepository reviewImagePathRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookRepository bookRepository;

    @Test
    @DisplayName("리뷰 생성 서비스 실패 - 해당 유저가 존재하지 않음")
    void createReview_fail_NoUser() {
        Long bookId = 1L;
        Long userId = 2L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.createReview(dto, bookId, userId));
    }

    @Test
    @DisplayName("리뷰 생성 서비스 실패 - 해당 도서가 존재하지 않음")
    void createReview_fail_NoBook() {
        Long bookId = 1L;
        Long userId = 2L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");

        User user = User.builder().userId(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.createReview(dto, bookId, userId));
    }

    @Test
    @DisplayName("리뷰 생성 서비스 실패 - 해당 도서에 대한 주문내역이 없음")
    void createReview_fail_NotOrder() {
        Long bookId = 1L;
        Long userId = 2L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");

        User user = User.builder().userId(userId).build();
        Book book = Book.builder().bookId(bookId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.bookOrderCheck(userId, bookId)).thenReturn(0L);


        NotCreateReviewException exception = assertThrows(NotCreateReviewException.class, () -> {
            reviewService.createReview(dto, bookId, userId);
        });


        assertEquals("주문한 도서에 포함되어 있지 않습니다.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("리뷰 생성 서비스 실패 - 해당 도서에 대한 리뷰를 이미 유저가 작성함")
    void createReview_fail_AlreadyReviewExist() {
        Long bookId = 1L;
        Long userId = 2L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");

        User user = User.builder().userId(userId).build();
        Book book = Book.builder().bookId(bookId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.bookOrderCheck(userId, bookId)).thenReturn(1L);
        when(reviewRepository.alreadyExistCheck(userId, bookId)).thenReturn(1L);
//        when(reviewService.canReviewBeCreated(userId, bookId)).thenReturn("REVIEW_ALREADY_EXIST");


        NotCreateReviewException exception = assertThrows(NotCreateReviewException.class, () -> {
            reviewService.createReview(dto, bookId, userId);
        });


        assertEquals("해당 도서에 대한 리뷰는 한번만 쓸 수 있습니다.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


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
        when(reviewRepository.bookOrderCheck(userId, bookId)).thenReturn(1L);
        when(reviewRepository.alreadyExistCheck(userId, bookId)).thenReturn(0L);
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
    @DisplayName("리뷰 수정 서비스 실패 - 리뷰가 존재하지 않음")
    void updateReview_fail_NoReview() {
        Long reviewId = 1L;
        ReviewRequestDTO dto = new ReviewRequestDTO(4, "Updated Title", "Updated Content");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> reviewService.updateReview(dto, reviewId));
        verify(reviewRepository, never()).save(any());
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
    @DisplayName("리뷰 정렬 조회 - 실패 (도서 없음)")
    void getReviewsByBookOrderBySort_fail_bookNotFound() {
        Long bookId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String sort = "score";

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort));

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoInteractions(reviewRepository);
    }


    @Test
    @DisplayName("리뷰 정렬 조회 - 성공 (score 기준)")
    void getReviewsByBookOrderBySort_score_success() {
        Long bookId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String sort = "score";

        Book book = Book.builder().bookId(bookId).build();
        Page<Object[]> mockResults = new PageImpl<>(List.of(
        ));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookOrderByScoreDesc(eq(userId), eq(bookId), any(Pageable.class))).thenReturn(mockResults);

        Page<ReviewLikeCountDTO> result = reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).findAllByBookOrderByScoreDesc(eq(userId), eq(bookId), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 정렬 조회 - 성공 (recommend 기준)")
    void getReviewsByBookOrderBySort_recommend_success() {
        Long bookId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String sort = "recommend";

        Book book = Book.builder().bookId(bookId).build();
        Page<Object[]> mockResults = new PageImpl<>(List.of(

        ));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookOrderByLikeDesc(eq(userId), eq(bookId), any(Pageable.class))).thenReturn(mockResults);

        Page<ReviewLikeCountDTO> result = reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).findAllByBookOrderByLikeDesc(eq(userId), eq(bookId), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 정렬 조회 - 성공 (createdAt 기준)")
    void getReviewsByBookOrderBySort_createdAt_success() {
        Long bookId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String sort = "createdAt";

        Book book = Book.builder().bookId(bookId).build();
        Page<Object[]> mockResults = new PageImpl<>(List.of(
        ));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(eq(userId), eq(bookId), any(Pageable.class))).thenReturn(mockResults);

        Page<ReviewLikeCountDTO> result = reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findById(bookId);
        verify(reviewRepository, times(1)).findAllByBookOrderByCreatedAtDesc(eq(userId), eq(bookId), any(Pageable.class));
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
    @DisplayName("해당 도서에 대한 전체 리뷰 조회 실패 - (도서 없음)")
    void getReviewsByBook_fail_bookNotFound() {
        Long bookId = 1L;
        int page = 0;
        int size = 10;

        when(bookRepository.findById(any())).thenThrow(new NotFoundException("존재하지 않는 책입니다."));

        assertThrows(NotFoundException.class, () -> reviewService.getReviewsByBook(bookId, page, size));
        verify(reviewRepository, never()).findAllByBook(any(), any(Pageable.class));
    }


    @Test
    @DisplayName("해당 리뷰 조회 실패 - 리뷰 존재하지 않음")
    void getReviewById_fail_NotExistReview() {
        Long reviewId = 1L;


        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewById(reviewId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("해당 리뷰 조회 성공")
    void getReviewById_success() {
        Long reviewId = 1L;

        Review review = Review.builder().reviewId(reviewId).score(5).title("Title").content("Content").build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewResponseDTO result = reviewService.getReviewById(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        verify(reviewRepository, times(1)).findById(reviewId);
    }


    @Test
    @DisplayName("유저 리뷰 가져오기 실패 - 유저가 존재하지 않음")
    void getUserReviews_fail_NotExistUser() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        Page<Object[]> results = new PageImpl<>(List.of(
        ));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.getUserReviews(userId, page, size));


        assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
        verify(reviewRepository, never()).getUserReviews(eq(userId), any(Pageable.class));
    }


    @Test
    @DisplayName("유저 리뷰 가져오기 - 성공")
    void getUserReviews_success() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        User user = User.builder().userId(userId).build();
        Page<Object[]> results = new PageImpl<>(List.of(
        ));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.getUserReviews(eq(userId), any(Pageable.class))).thenReturn(results);

        Page<UserReviewsDTO> result = reviewService.getUserReviews(userId, page, size);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(userRepository, times(1)).findById(userId);
        verify(reviewRepository, times(1)).getUserReviews(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 가능한 도서 조회 성공 테스트")
    void getAvailableReviews_Success() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        User mockUser = User.builder().userId(userId).userName("Test User").build();
        Object[] bookData = new Object[]{1L, "Book Title", "Author", "Path", Timestamp.valueOf(LocalDateTime.now())}; // Timestamp 사용
        Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(bookData), PageRequest.of(page, size), 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(reviewRepository.getEligibleBooksForReview(userId, PageRequest.of(page, size))).thenReturn(mockPage);

        Page<UserAvailableReviewsDTO> result = reviewService.getAvailableReviews(userId, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Book Title", result.getContent().get(0).getTitle());
    }


    @Test
    @DisplayName("리뷰 가능한 도서 조회 실패 - 유저 없음")
    void getAvailableReviews_UserNotFound() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.getAvailableReviews(userId, page, size));

        assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
        verify(reviewRepository, never()).getEligibleBooksForReview(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("포토 리뷰 여부 확인 - 성공")
    void isPhotoReview_success() {
        Long reviewId = 1L;

        Review existingReview = Review.builder()
                .reviewId(reviewId)
                .score(5)
                .title("Old Title")
                .content("Old Content")
                .createdAt(LocalDateTime.now()).build();

        ReviewImagePath reviewImagePath = new ReviewImagePath(1L, "/review/reviewImage.png", existingReview);

        when(reviewImagePathRepository.findById(reviewId)).thenReturn(Optional.of(reviewImagePath));

        boolean result = reviewService.isPhotoReview(reviewId);

        assertTrue(result);
        verify(reviewImagePathRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("포토 리뷰 여부 확인 - 실패")
    void isPhotoReview_fail() {
        Long reviewId = 1L;

        when(reviewImagePathRepository.findById(reviewId)).thenReturn(Optional.empty());

        boolean result = reviewService.isPhotoReview(reviewId);

        assertFalse(result);
        verify(reviewImagePathRepository, times(1)).findById(reviewId);
    }


    @Test
    @DisplayName("리뷰 삭제 서비스 실패 - 리뷰가 존재하지 않음")
    void deleteReview_fail_NoReview() {
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");

        verify(reviewRepository, never()).deleteById(any());


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


    @Test
    @DisplayName("convertToReviewLikeCountDTO 변환 테스트")
    void convertToReviewLikeCountDTO() {
        // Given
        Long userId = 1L;
        Object[] reviewData = new Object[]{
                1L, "Review Title", "Review Content",
                Timestamp.valueOf(LocalDateTime.now()), "Test User", userId,
                5, 10L, 2L, "image1,image2", 1
        };
        Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(reviewData), PageRequest.of(0, 10), 1);

        // When
        Page<ReviewLikeCountDTO> result = reviewService.convertToReviewLikeCountDTO(mockPage, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ReviewLikeCountDTO dto = result.getContent().get(0);
        assertEquals("Review Title", dto.getTitle());
        assertEquals("Review Content", dto.getContent());
        assertEquals(5, dto.getScore());
        assertEquals(10L, dto.getLikeCount());
        assertEquals(2L, dto.getCommentCount());
        assertEquals(Arrays.asList("image1", "image2"), dto.getImagePaths());
        assertTrue(dto.isEditable());
        assertTrue(dto.isDeletable());
        assertTrue(dto.isUserLiked());
    }

    @Test
    @DisplayName("convertToUserReviewsDTO 변환 테스트")
    void convertToUserReviewsDTO() {
        // Given
        Object[] reviewData = new Object[]{
                1L, 101L, "Book Title", "Author Name", "image/path",
                "Review Title", "Review Content", Timestamp.valueOf(LocalDateTime.now()),
                "Test User", 1L, 5, 10L, 2L, "img1,img2"
        };
        Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(reviewData), PageRequest.of(0, 10), 1);

        // When
        Page<UserReviewsDTO> result = reviewService.convertToUserReviewsDTO(mockPage);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        UserReviewsDTO dto = result.getContent().get(0);
        assertEquals("Book Title", dto.getBookTitle());
        assertEquals("Review Title", dto.getTitle());
        assertEquals(5, dto.getScore());
        assertEquals(10L, dto.getLikeCount());
        assertEquals(Arrays.asList("img1", "img2"), dto.getImagePaths());
    }

    @Test
    @DisplayName("convertToUserAvailableReviewsDTO 변환 테스트")
    void convertToUserAvailableReviewsDTO() {
        // Given
        Object[] bookData = new Object[]{
                101L, "Book Title", "Author Name", "image/path",
                Timestamp.valueOf(LocalDateTime.now())
        };
        Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(bookData), PageRequest.of(0, 10), 1);

        // When
        Page<UserAvailableReviewsDTO> result = reviewService.convertToUserAvailableReviewsDTO(mockPage);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        UserAvailableReviewsDTO dto = result.getContent().get(0);
        assertEquals("Book Title", dto.getTitle());
        assertEquals("Author Name", dto.getContributor());
    }

    @Test
    @DisplayName("isLikedByUser 테스트")
    void isLikedByUser() {
        // Test case 1: likedByUser is 1
        Integer likedByUser = 1;
        assertTrue(reviewService.isLikedByUser(likedByUser));

        // Test case 2: likedByUser is 0
        likedByUser = 0;
        assertFalse(reviewService.isLikedByUser(likedByUser));

        // Test case 3: likedByUser is null
        likedByUser = null;
        assertFalse(reviewService.isLikedByUser(likedByUser));
    }


}