package com.simsimbookstore.apiserver.books.book.dto;


import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookStatusResponseDto {

    private BookStatus bookStatus;
}
