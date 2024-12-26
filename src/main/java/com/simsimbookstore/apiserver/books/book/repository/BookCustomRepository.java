package com.simsimbookstore.apiserver.books.book.repository;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookCustomRepository {
    List<BookListResponse> getNewBookList();

    Page<BookListResponse> getAllBook(Pageable pageable);

    Page<BookListResponse> getBookListByCategory(Long userId, Long categoryId, Pageable pageable);

    BookResponseDto getBookDetail(Long userId, Long bookId);

    List<Long> getLowestCategoryId(List<Long> categoryIdList);

    Page<BookListResponse> getUserLikeBook(Pageable pageable, Long userId);


    List<BookListResponse> getBooksForCheck(List<Long> bookIdList);

    List<BookListResponse> getBestSeller();


}
