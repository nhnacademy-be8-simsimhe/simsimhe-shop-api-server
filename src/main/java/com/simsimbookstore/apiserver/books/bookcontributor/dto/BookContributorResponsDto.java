package com.simsimbookstore.apiserver.books.bookcontributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookContributorResponsDto {

    private Long contributorId;

    private String contributorName;

    private String contributorRole;
}
