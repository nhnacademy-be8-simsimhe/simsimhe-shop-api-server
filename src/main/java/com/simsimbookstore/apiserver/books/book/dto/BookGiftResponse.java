package com.simsimbookstore.apiserver.books.book.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookGiftResponse {

    private boolean giftPackaging;
}
