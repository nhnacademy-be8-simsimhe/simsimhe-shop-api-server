package com.simsimbookstore.apiserver.books.bookcontributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookContributorResponsDto {

    private Long contributorId;

    private String contributorName;

    private String contributorRole;
}
