package com.simsimbookstore.apiserver.books.book.service;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookGetService {

    private final BookRepository bookRepository;


    /**
     * 모든 도서 조회
     *
     * @param pageable
     * @return
     */
//    public Page<BookListResponse> getAllBook(Pageable pageable) {
//        return bookRepository.getAllBook(pageable);
//    }
    public PageResponse<BookListResponse> getAllBook(Pageable pageable) {
        Page<BookListResponse> responses = bookRepository.getAllBook(pageable);
        return this.getPageResponse(pageable.getPageSize(),responses);
    }

    /**
     * 가장 최신 출판된 8개의 책 조회
     *
     * @return
     */
    public List<BookListResponse> getNewBooks() {
        return bookRepository.getNewBookList();
    }

    /**
     * 책 상세 정보 조회 메서드
     *
     * @param userId
     * @param bookId
     * @return
     */
    @Transactional
    public BookResponseDto getBookDetail(Long userId, Long bookId) {
        BookResponseDto bookDetail = bookRepository.getBookDetail(userId, bookId);
        return bookDetail;
    }

    /**
     * 주문 전에 책 상태를 조회하는 메서드
     *
     * @param bookIdList
     * @return
     */
    public List<BookListResponse> getBooksForCheck(List<Long> bookIdList) {
        return bookRepository.getBooksForCheck(bookIdList);
    }

    /**
     * 회원이 좋아요한 책을 조회
     *
     * @param userId
     * @param pageable
     * @return
     */
    public Page<BookListResponse> getUserLikeBook(Long userId, Pageable pageable) {
        return bookRepository.getUserLikeBook(pageable, userId);
    }

    /**
     * 카테고리와 하위 카테고라의 책을 모두 조회
     *
     * @param userId
     * @param categoryId
     * @param pageable
     * @return
     */
    public Page<BookListResponse> getBooksByCategory(Long userId, Long categoryId, Pageable pageable) {
        return bookRepository.getBookListByCategory(userId, categoryId, pageable);
    }

    /**
     * 툭정 태그에 맞는 도서조회
     *
     * @param userId
     * @param tagId
     * @param pageable
     * @return
     */
    public Page<BookListResponse> getBooksByTag(Long userId, Long tagId, Pageable pageable) {
        return bookRepository.getBookListByTag(userId, tagId, pageable);
    }

    /**
     * Paging하여 응답객체를 생성하는 메서드 입니다.
     *
     * @param page     페이지 번호
     * @param bookPage 페이징할 페이지 객체
     * @return PageResponse<BookDto.ListResponse>
     */
    private PageResponse<BookListResponse> getPageResponse(int page,
                                                           Page<BookListResponse> bookPage) {

        // 최대 버튼 개수 8개
        int maxPageButtons = 8;
        int startPage = (int) Math.max(1, bookPage.getNumber() - Math.floor((double) maxPageButtons / 2));
        int endPage = Math.min(startPage + maxPageButtons - 1, bookPage.getTotalPages());

        if (endPage - startPage + 1 < maxPageButtons) {
            startPage = Math.max(1, endPage - maxPageButtons + 1);
        }


        return PageResponse.<BookListResponse>builder()
                .data(bookPage.getContent())
                .currentPage(page)
                .startPage(startPage)
                .endPage(endPage)
                .totalPage(bookPage.getTotalPages())
                .totalElements(bookPage.getTotalElements())
                .build();
    }

}
