package com.simsimbookstore.apiserver.books.bookcontributor.dto;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookContributorRequestDto {

    private Contributor contributor;

    private Book book;
}
