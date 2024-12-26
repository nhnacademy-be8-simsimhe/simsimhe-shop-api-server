package com.simsimbookstore.apiserver.books.book.service;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookGetService {

    private final BookRepository bookRepository;

    public Page<BookListResponse> getAllBook(Pageable pageable) {
        // QueryDSL로 데이터 조회
        Page<BookListResponse> responses = bookRepository.getAllBook(pageable)
                .map(book -> BookListResponse.builder()
                        .bookId(book.getBookId())
                        .title(book.getTitle())
                        .publicationDate(book.getPublicationDate())
                        .price(book.getPrice())
                        .saleprice(book.getSaleprice())
                        .publisher(book.getPublisher())
                        .bookStatus(book.getBookStatus())
                        .quantity(book.getQuantity())
                        .bookLikeId(book.getBookLikeId())
                        .isLiked(book.isLiked())
                        .contributorRoleList(book.getContributorRoleList())
                        .build()
                );

        return responses;
    }
}
