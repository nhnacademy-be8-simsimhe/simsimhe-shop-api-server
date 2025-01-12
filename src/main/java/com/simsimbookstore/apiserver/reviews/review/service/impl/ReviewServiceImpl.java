package com.simsimbookstore.apiserver.reviews.review.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewImagePathRepository reviewImagePathRepository;




    @Override
    public Review createReview(ReviewRequestDTO dto, Long bookId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 책입니다."));


        Review review = Review.builder()
                .score(dto.getScore())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .book(book)
                .build();


        Review createReview = reviewRepository.save(review);

        return createReview;
    }

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

    @Override
    public Page<Review> getAllReviews(Long bookId, int page, int size) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 책입니다."));

        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findAllByBook(book, pageable);
    }


    @Override
    public Page<ReviewLikeCountDTO> getReviewsByBookOrderByScore(Long bookId, int page, int size) {

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 도서입니다."));

        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findAllByBookOrderByScoreDesc(bookId, pageable);
    }

    @Override
    public Page<ReviewLikeCountDTO> getReviewsByBookOrderByLike(Long bookId, int page, int size) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 도서입니다."));

        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findAllByBookOrderByLikeDesc(bookId, pageable);
    }

    @Override
    public Page<ReviewLikeCountDTO> getReviewsByBookOrderByRecent(Long bookId, int page, int size) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 도서입니다."));

        Pageable pageable = PageRequest.of(page, size);

        Page<Object[]> results = reviewRepository.findAllByBookOrderByCreatedAtDesc(bookId,pageable);


        return convertToReviewLikeCountDTO(results, pageable);

//        return reviewRepository.findAllByBookOrderByCreatedAtDesc(bookId, pageable);
    }

    @Override
    public Page<ReviewLikeCountDTO> getReviewsByUser(Long userId, Long bookId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 도서입니다."));

        Pageable pageable = PageRequest.of(page, size);


        return reviewRepository.findAllByUser(user, pageable);
    }


    public Page<Review> getReviewsByBook(Long bookId, int page, int size){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 책입니다."));

        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findAllByBook(book, pageable);
    }


    @Transactional
    @Override
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {

            return new ReviewResponseDTO(reviewOptional.get());
        }

        throw new NotFoundException("존재하지 않는 책입니다.");
    }


    @Override
    public void deleteReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));
        reviewRepository.delete(existingReview);
    }


    public Page<ReviewLikeCountDTO> convertToReviewLikeCountDTO(Page<Object[]> results, Pageable pageable) {
        return results.map(obj -> new ReviewLikeCountDTO(
                (Long) obj[0],                 // reviewId
                (String) obj[1],              // title
                (String) obj[2],              // content
                obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null,       // createdAt
                (String) obj[4],              // userName
                (Integer) obj[5],             // score
                ((Long) obj[6]).longValue(), // likeCount
                ((Long) obj[7]).longValue(), // commentCount
                obj[8] != null ? Arrays.asList(((String) obj[8]).split(",")) : null// imagePaths
        ));
    }

    @Override
    public boolean isPhotoReview(Long reviewId) {
        return reviewImagePathRepository.findById(reviewId).isPresent();
    }
}
