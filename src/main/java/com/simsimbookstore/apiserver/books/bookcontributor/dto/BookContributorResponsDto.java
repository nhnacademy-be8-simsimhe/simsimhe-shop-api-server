package com.simsimbookstore.apiserver.books.bookcontributor.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookContributorResponsDto {

    private Long contributorId;

    private String contributorName;

    private String contributorRole;
}
