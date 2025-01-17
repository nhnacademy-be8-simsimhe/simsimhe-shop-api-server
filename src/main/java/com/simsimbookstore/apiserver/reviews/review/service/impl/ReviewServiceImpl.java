package com.simsimbookstore.apiserver.reviews.review.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.reviews.review.dto.*;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.exception.NotCreateReviewException;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewImagePathRepository reviewImagePathRepository;
    private final PointHistoryService pointHistoryService;
    private final EntityManager em;


    @Transactional
    @Override
    public Review createReview(ReviewRequestDTO dto, Long bookId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 책입니다."));


        String canReviewBeCreated = canReviewBeCreated(userId, bookId);
        if (!canReviewBeCreated.equals("REVIEW_CAN_CREATE")) {
            String message = canReviewBeCreated.equals("REVIEW_ALREADY_EXIST") ? "해당 도서에 대한 리뷰는 한번만 쓸 수 있습니다." : "주문한 도서에 포함되어 있지 않습니다.";
            throw new NotCreateReviewException(message);
        }


        Review review = Review.builder()
                .score(dto.getScore())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .book(book)
                .build();


        Review createReview = reviewRepository.save(review);

        //TODO: 테스트 후 다시 사용할 코드입니다.
        //리뷰 포인트 적립
//        ReviewPointCalculateRequestDto pointDto = new ReviewPointCalculateRequestDto (createReview.getReviewId(), userId);
//        pointHistoryService.reviewPoint(pointDto);

        return createReview;
    }

    @Override
    public Page<ReviewLikeCountDTO> getReviewsByBookOrderBySort(Long bookId, Long userId, int page, int size, String sort) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 도서입니다."));


        Pageable pageable = PageRequest.of(page, size);

        Page<Object[]> results = null;

        if (sort.equals("score")) {
            results = reviewRepository.findAllByBookOrderByScoreDesc(userId, bookId, pageable);
        } else if (sort.equals("recommend")) {
            results = reviewRepository.findAllByBookOrderByLikeDesc(userId, bookId, pageable);
        } else {
            results = reviewRepository.findAllByBookOrderByCreatedAtDesc(userId, bookId, pageable);
        }


        return convertToReviewLikeCountDTO(results, userId);
    }


    @Override
    public Page<Review> getReviewsByBook(Long bookId, int page, int size) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 책입니다."));

        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findAllByBook(book, pageable);
    }


    @Override
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {

            return new ReviewResponseDTO(reviewOptional.get());
        }

        throw new NotFoundException("존재하지 않는 책입니다.");
    }


    @Override
    public Page<UserReviewsDTO> getUserReviews(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = reviewRepository.getUserReviews(userId, pageable);
        return convertToUserReviewsDTO(results);
    }

    @Override
    public Page<UserAvailableReviewsDTO> getAvailableReviews(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = reviewRepository.getEligibleBooksForReview(userId, pageable);

        return convertToUserAvailableReviewsDTO(results);
    }


    @Transactional
    @Override
    public Review updateReview(ReviewRequestDTO dto, Long reviewId) {

        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));


        existingReview.setScore(dto.getScore());
        existingReview.setTitle(dto.getTitle());
        existingReview.setContent(dto.getContent());
        existingReview.setUpdateAt(LocalDateTime.now());

        return reviewRepository.save(existingReview);
    }


    @Transactional
    @Override
    public void deleteReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));
        reviewRepository.delete(existingReview);
    }


    public Page<ReviewLikeCountDTO> convertToReviewLikeCountDTO(Page<Object[]> results, Long userId) {
        return results.map(obj -> ReviewLikeCountDTO.builder()
                .reviewId((Long) obj[0])
                .title((String) obj[1])
                .content((String) obj[2])
                .createdAt(obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null)
                .userName((String) obj[4])
                .userId((Long) obj[5])
                .score((Integer) obj[6])
                .likeCount(((Long) obj[7]))
                .commentCount(((Long) obj[8]))
                .imagePaths(obj[9] != null ? Arrays.asList(((String) obj[9]).split(",")) : null)
                .editable(((Long) obj[5]).equals(userId))
                .deletable(((Long) obj[5]).equals(userId))
                .userLiked(isLikedByUser((Integer) obj[10]))
                .build()
        );
    }


    public Page<UserReviewsDTO> convertToUserReviewsDTO(Page<Object[]> results) {
        return results.map(obj -> UserReviewsDTO.builder()
                .reviewId((Long) obj[0])
                .bookId((Long) obj[1])
                .bookTitle((String) obj[2])
                .contributor((String) obj[3])
                .bookImagePath((String) obj[4])
                .title((String) obj[5])
                .content((String) obj[6])
                .createdAt(obj[7] != null ? ((Timestamp) obj[7]).toLocalDateTime() : null)
                .userName((String) obj[8])
                .userId((Long) obj[9])
                .score((Integer) obj[10])
                .likeCount(((Long) obj[11]))
                .commentCount(((Long) obj[12]))
                .imagePaths(obj[13] != null ? Arrays.asList(((String) obj[13]).split(",")) : null)
                .build());
    }


    public Page<UserAvailableReviewsDTO> convertToUserAvailableReviewsDTO(Page<Object[]> results) {
        return results.map(obj ->
                UserAvailableReviewsDTO.builder()
                        .bookId((Long) obj[0])
                        .title((String) obj[1])
                        .contributor((String) obj[2])
                        .bookImagePath((String) obj[3])
                        .orderDate(obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime() : null)
                        .build()
        );
    }

    public String canReviewBeCreated(Long userId, Long bookId) {

        long orderCheck = reviewRepository.bookOrderCheck(userId, bookId);

        if (orderCheck == 0)
            return "ORDER_NOT_FOUND";


        long reviewExists = reviewRepository.alreadyExistCheck(userId, bookId);
        return reviewExists == 0 ? "REVIEW_CAN_CREATE" : "REVIEW_ALREADY_EXIST";
    }


    public Boolean isLikedByUser(Integer likedByUser) {

        return likedByUser != null && likedByUser == 1;
    }

    @Override
    public boolean isPhotoReview(Long reviewId) {
        return reviewImagePathRepository.findById(reviewId).isPresent();
    }
}
