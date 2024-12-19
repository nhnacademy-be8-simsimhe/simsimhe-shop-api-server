package com.simsimbookstore.apiserver.books.book.mapper;

import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;
//import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.Book;

import java.time.LocalDate;

public class BookMapper {

//    public static BookResponseDto toResponseDto(Book book){
//
//    }

    public static Book toBook(BookRequestDto bookRequestDto){
        return Book.builder()
                .title(bookRequestDto.getTitle())
                .description(bookRequestDto.getDescription())
                .bookIndex(bookRequestDto.getBookIndex())
                .publisher(bookRequestDto.getPublisher())
                .isbn(bookRequestDto.getIsbn())
                .quantity(bookRequestDto.getQuantity())
                .price(bookRequestDto.getPrice())
                .saleprice(bookRequestDto.getSaleprice())
                .publicationDate(LocalDate.from(bookRequestDto.getPublicationDate()))
                .giftPackaging(true)
                .pages(bookRequestDto.getPages())
                .bookStatus(bookRequestDto.getBookStatus())
                .build();
    }
}
