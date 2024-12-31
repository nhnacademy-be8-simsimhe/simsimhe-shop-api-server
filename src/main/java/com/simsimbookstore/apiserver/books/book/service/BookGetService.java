package com.simsimbookstore.apiserver.books.book.service;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookGetService {

    private final BookRepository bookRepository;

    /**
     * 책 수정을 위한 책 객체에 대한 정보를 얻는 메서드
     *
     * @param bookId 책 아이디
     * @return BookDto.Response
     */
    public BookResponseDto getUpdateBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {

            Book book = optionalBook.get();
            return BookResponseDto.builder()
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .bookIndex(book.getBookIndex())
                    .description(book.getDescription())
                    .publicationDate(book.getPublicationDate())
                    .price(book.getPrice())
                    .saleprice(book.getSaleprice())
                    .isbn(book.getIsbn())
                    .pages(book.getPages())
                    .quantity(book.getQuantity())
                    .giftPackaging(book.isGiftPackaging())
                    .publisher(book.getPublisher())
                    .build();

        } else {
            throw new NotFoundException("책 정보가 없습니다.");
        }
    }


    /**
     * 모든 도서 조회
     *
     * @param pageable
     * @return
     */
    public PageResponse<BookListResponse> getAllBook(Pageable pageable) {
        Page<BookListResponse> responses = bookRepository.getAllBook(pageable);
        return this.getPageResponse(pageable.getPageNumber(), responses);
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
        return bookRepository.getBookDetail(userId, bookId);
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
    public PageResponse<BookListResponse> getUserLikeBook(Long userId, Pageable pageable) {
        Page<BookListResponse> userLikeBook = bookRepository.getUserLikeBook(pageable, userId);

        return this.getPageResponse(pageable.getPageNumber(), userLikeBook);
    }

    /**
     * 카테고리와 하위 카테고라의 책을 모두 조회
     *
     * @param userId
     * @param categoryId
     * @param pageable
     * @return
     */
    public PageResponse<BookListResponse> getBooksByCategory(Long userId, Long categoryId, Pageable pageable) {
        Page<BookListResponse> bookListByCategory = bookRepository.getBookListByCategory(userId, categoryId, pageable);

        return this.getPageResponse(pageable.getPageNumber(), bookListByCategory);
    }

    /**
     * 툭정 태그에 맞는 도서조회
     *
     * @param userId
     * @param tagId
     * @param pageable
     * @return
     */
    public PageResponse<BookListResponse> getBooksByTag(Long userId, Long tagId, Pageable pageable) {
        Page<BookListResponse> bookListByTag = bookRepository.getBookListByTag(userId, tagId, pageable);
        return this.getPageResponse(pageable.getPageNumber(), bookListByTag);
    }

    /**
     * 주문량이 많은 6개도서
     *
     * @return
     */
    public List<BookListResponse> getPopularityBook() {
        return bookRepository.getPopularityBook();
    }

    /**
     * 특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능
     *
     * @param categoryIdList
     * @param bookId
     * @return
     */
    public List<BookListResponse> getRecommendBooks(List<Long> categoryIdList, Long bookId) {
        return bookRepository.getRecommendBook(categoryIdList, bookId);
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


        //최대 버튼개수 8개
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
