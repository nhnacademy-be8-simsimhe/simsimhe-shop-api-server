package com.simsimbookstore.apiserver.books.book.mapper;

import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;

import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.Book;


import java.time.LocalDate;
import java.util.Optional;


public class BookMapper {

    // private 생성자를 추가하여 인스턴스화를 방지(소나큐브 경고처리)
    private BookMapper() {
        throw new UnsupportedOperationException("BookMapper클래스 인스턴스화 할 수 없음");
    }

    public static BookResponseDto toResponseDto(Book book) {
        return BookResponseDto.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .description(book.getDescription())
                .bookIndex(book.getBookIndex())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .viewCount(book.getViewCount())
                .price(book.getPrice())
                .saleprice(book.getSaleprice())
                .publicationDate(book.getPublicationDate())
                .pages(book.getPages())
                .quantity(book.getQuantity())
                .bookStatus(book.getBookStatus())
                .build();
    }

    public static Book toBook(BookRequestDto bookRequestDto) {
        return Book.builder()
                .title(bookRequestDto.getTitle())
                .description(bookRequestDto.getDescription())
                .bookIndex(bookRequestDto.getBookIndex())
                .publisher(bookRequestDto.getPublisher()) //출판사
                .isbn(bookRequestDto.getIsbn())
                .quantity(bookRequestDto.getQuantity())
                .price(bookRequestDto.getPrice())
                .saleprice(bookRequestDto.getSaleprice())
                .publicationDate(Optional.ofNullable(bookRequestDto.getPublicationDate())
                        .map(LocalDate::from)
                        .orElse(LocalDate.now())) // null인 경우 현재 날짜
                .giftPackaging(bookRequestDto.isGiftPackaging())
                .pages(bookRequestDto.getPages())
                .bookStatus(bookRequestDto.getBookStatus())
                .viewCount(0L)
                .build();
    }
}
