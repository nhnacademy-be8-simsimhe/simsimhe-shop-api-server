package com.simsimbookstore.apiserver.books.contributor.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ContributorResponseDto {

    private Long contributorId;

    private String contributorName;

    private String contributorRole;

}
