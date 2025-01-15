package com.simsimbookstore.apiserver.books.contributor.mapper;

import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;

public class ContributorMapper {

    private ContributorMapper() {
        throw new UnsupportedOperationException("ContributorMapper는 인스턴스화 할수없음");
    }

    public static ContributorResponseDto toResponse(Contributor contributor) {
        return ContributorResponseDto.builder()
                .contributorId(contributor.getContributorId())
                .contributorName(contributor.getContributorName())
                .contributorRole(contributor.getContributorRole())
                .build();
    }

    public static Contributor toContributor(ContributorRequestDto contributorRequestDto) {
        return Contributor.builder()
                .contributorName(contributorRequestDto.getContributorName())
                .contributorRole(contributorRequestDto.getContributorRole())
                .build();
    }
}
