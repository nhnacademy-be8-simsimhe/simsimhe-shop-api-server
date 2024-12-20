package com.simsimbookstore.apiserver.books.contributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContributorResponseDto {

    private Long contributorId;

    private String contributorName;

    private String contributorRole;

}
