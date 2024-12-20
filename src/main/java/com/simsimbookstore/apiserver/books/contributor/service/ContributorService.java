package com.simsimbookstore.apiserver.books.contributor.service;

import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContributorService {

    ContributorResponseDto saveContributor(ContributorRequestDto contributorRequestDto);

    List<ContributorResponseDto> getAllContributor();

    Page<ContributorResponseDto> getAllContributors(Pageable pageable);

    void deleteContributor(Long contributorId);

    Contributor findById(Long contributorId);


}
