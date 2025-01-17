package com.simsimbookstore.apiserver.books.book.controller;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.book.service.BookGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/books")
public class BookGetController {

    private final BookGetService bookGetService;

    /**
     * 프론트에서 수정하기 위해 책을 단건 조회하는 로직
     *
     * @param bookId
     * @return
     */
    @GetMapping("/{bookId}/update")
    public ResponseEntity<BookResponseDto> getBookByIdForUpdate(@PathVariable("bookId") Long bookId) {

        BookResponseDto response = bookGetService.getUpdateBook(bookId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 관리자 도서 목록에서 사용할 모든 책을 조회 페이징처리
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResponse<BookListResponse>> getAllBooks(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("title"));
        PageResponse<BookListResponse> allBook = bookGetService.getAllBook(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(allBook);
    }


    /**
     * 가장 최근 출판된 책 8권을 조회
     *
     * @return
     */
    @GetMapping("/new")
    public ResponseEntity<List<BookListResponse>> getNewBooks() {
        List<BookListResponse> newBooks = bookGetService.getNewBooks();
        return ResponseEntity.status(HttpStatus.OK).body(newBooks);
    }

    /**
     * 도서 상세조회
     *
     * @param bookId
     * @param userId 사용자가 도서 좋아요를 눌렀는지 확인하려고
     * @return
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable(name = "bookId") Long bookId, @RequestParam(required = false) Long userId) {
        BookResponseDto bookDetail = bookGetService.getBookDetail(userId, bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookDetail);
    }

    /**
     * 주문전에 책의 재고를 확인하는 메서드
     *
     * @param bookIdList
     * @return
     */
    @GetMapping("/check")
    public ResponseEntity<List<BookListResponse>> getBooksForQuantityCheck(@RequestParam(name = "bookIdList") List<Long> bookIdList) {
        List<BookListResponse> booksForCheck = bookGetService.getBooksForCheck(bookIdList);
        return ResponseEntity.status(HttpStatus.OK).body(booksForCheck);
    }

    /**
     * 회원이 좋아요한 책을 조회
     *
     * @param page
     * @param size
     * @param userId
     * @return
     */
    @GetMapping("/like/user/{userId}")
    public ResponseEntity<PageResponse<BookListResponse>> getUserLikeBook(@RequestParam(defaultValue = "1") int page,
                                                                          @RequestParam(defaultValue = "5") int size,
                                                                          @PathVariable(name = "userId") Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PageResponse<BookListResponse> userLikeBook = bookGetService.getUserLikeBook(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userLikeBook);

    }

    /**
     * 카테고리와 하위 카테고리에 해당하는 책을 조회
     *
     * @param categoryId
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<BookListResponse>> getBooksByCategory(@PathVariable(name = "categoryId") Long categoryId,
                                                                             @RequestParam(required = false) Long userId,
                                                                             @RequestParam(defaultValue = "1") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "latest") String sort) {
        Pageable pageable = this.createPageable(page - 1, size, sort);
        PageResponse<BookListResponse> booksByCategory = bookGetService.getBooksByCategory(userId, categoryId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(booksByCategory);
    }


    /**
     * 주문량이 많은 도서 6개 조회
     *
     * @return
     */
    @GetMapping("/popularity")
    public ResponseEntity<List<BookListResponse>> getPopularityBook() {
        List<BookListResponse> popularityBook = bookGetService.getPopularityBook();
        return ResponseEntity.status(HttpStatus.OK).body(popularityBook);
    }

    /**
     * 특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능
     *
     * @param bookId
     * @param categoryIdList
     * @return
     */
    @GetMapping("/{bookId}/recommend")
    public ResponseEntity<List<BookListResponse>> getRecommendBooks(@PathVariable(name = "bookId") Long bookId,
                                                                    @RequestParam(name = "categoryIdList") List<Long> categoryIdList) {
        List<BookListResponse> recommendBooks = bookGetService.getRecommendBooks(categoryIdList, bookId);
        return ResponseEntity.status(HttpStatus.OK).body(recommendBooks);

    }


    /**
     * 특정 태그에 속한 도서조회
     *
     * @param tagId
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<PageResponse<BookListResponse>> getBooksByTag(@PathVariable(name = "tagId") Long tagId,
                                                                        @RequestParam(required = false) Long userId,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "16") int size,
                                                                        @RequestParam(defaultValue = "latest") String sort) { //정렬 기본은 최신순으로
        Pageable pageable = this.createPageable(page - 1, size, sort);
        PageResponse<BookListResponse> booksByTag = bookGetService.getBooksByTag(userId, tagId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(booksByTag);
    }

    // 동적 정렬을 위한 Pageable 생성 메서드
    private Pageable createPageable(int page, int size, String sort) {
        Sort sortOption;
        switch (sort.toLowerCase()) {
            case "price_high":
                sortOption = Sort.by(Sort.Direction.DESC, "price");
                break;
            case "price_low":
                sortOption = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "review":
                sortOption = Sort.by(Sort.Direction.DESC, "reviewCount"); // 리뷰 개수 정렬
                break;
            default: // 최신순
                sortOption = Sort.by(Sort.Direction.DESC, "publicationDate");
                break;
        }
        return PageRequest.of(page, size, sortOption);

    }
}
