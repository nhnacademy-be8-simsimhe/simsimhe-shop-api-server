package com.simsimbookstore.apiserver.like.service;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;
import com.simsimbookstore.apiserver.like.entity.BookLike;
import com.simsimbookstore.apiserver.like.repository.BookLikeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;


    /**
     * 책의 좋아요를 설정하는 메서드
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public BookLikeResponseDto setBookLike(BookLikeRequestDto requestDto) {
        Long userId = requestDto.getUserId();
        Long bookId = requestDto.getBookId();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("존재하지 않는 도서입니다"));

        Optional<BookLike> optionalBookLike = bookLikeRepository.findBookLike(book.getBookId(), user.getUserId());

        //회원이 도서에 좋아요를 안눌렀으면 눌렀다고 생성
        if(optionalBookLike.isEmpty()){
            BookLike bookLike = BookLike.builder()
                    .book(book)
                    .user(user)
                    .build();

            BookLike saveBookLike = bookLikeRepository.save(bookLike);

            return BookLikeResponseDto.builder()
                    .isbn(saveBookLike.getBook().getIsbn())
                    .userName(saveBookLike.getUser().getUserName())
                    .isLiked(true)
                    .build();
        }else {
            // 이미 눌러져있으면 좋아요취소
            BookLike bookLike = optionalBookLike.get();
            bookLikeRepository.deleteById(bookLike.getBookLikeId());

            return BookLikeResponseDto.builder()
                    .isLiked(false)
                    .build();

        }

    }

    /**
     * 회원이 누른 좋아요 총 개수 반환
     * @param userId
     * @return
     */
    @Override
    public Long getUserLikeNum(Long userId) {
        return bookLikeRepository.getUserLikeNum(userId);
    }
}
