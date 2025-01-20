package com.simsimbookstore.apiserver.books.book.dto;


import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookStatusResponseDto {

    private BookStatus bookStatus;
}
