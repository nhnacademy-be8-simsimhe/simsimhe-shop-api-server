package com.simsimbookstore.apiserver.books.bookcontributor.mapper;

import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorRequestDto;
import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorResponsDto;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;

import java.util.List;
import java.util.stream.Collectors;

public class BookContributorMapper {

    public static BookContributorResponsDto toResponse(BookContributor bookContributor) {
        return BookContributorResponsDto.builder()
                .contributorId(bookContributor.getBookContributorId())
                .contributorName(bookContributor.getContributor().getContributorName())
                .contributorRole(bookContributor.getContributor().getContributorRole())
                .build();
    }

    public static BookContributor toEntity(BookContributorRequestDto requestDto) {
        return BookContributor.builder()
                .contributor(requestDto.getContributor())
                .book(requestDto.getBook())
                .build();
    }

    public static List<BookContributorResponsDto> toResponseList(List<BookContributor> bookContributors) {
        return bookContributors.stream()
                .map(BookContributorMapper::toResponse)
                .collect(Collectors.toList());
    }
}
