package com.simsimbookstore.apiserver.books.contributor.service;

import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.error.ContributorNotFoundException;
import com.simsimbookstore.apiserver.books.contributor.mapper.ContributorMapper;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class ContributorServiceImpl implements ContributorService {

    private final ContributorRepositroy contributorRepositroy;

    public ContributorServiceImpl(ContributorRepositroy contributorRepositroy) {
        this.contributorRepositroy = contributorRepositroy;
    }

    /**
     * 기여자 등록 이미 등록되어있으면 등록되어있는 데이터를 반환하고 없는 데이터면 저장
     *
     * @param contributorRequestDto
     * @return
     */
    @Transactional
    @Override
    public ContributorResponseDto saveContributor(ContributorRequestDto contributorRequestDto) {
        Contributor contributor = ContributorMapper.toContributor(contributorRequestDto);

        Optional<Contributor> optionalContributor = contributorRepositroy.findByContributorName(contributor.getContributorName());

        if (optionalContributor.isPresent()) {
            Contributor findContributor = optionalContributor.get();
            return ContributorMapper.toResponse(findContributor);
        } else {
            Contributor saveContributor = contributorRepositroy.save(contributor);

            return ContributorMapper.toResponse(saveContributor);
        }

    }

    /**
     * 모든 기여자 조회
     *
     * @return
     */
    @Override
    public List<ContributorResponseDto> getAllContributor() {
        List<Contributor> contributors = contributorRepositroy.findAllContributors();
        return contributors.stream()
                .map(ContributorMapper::toResponse)
                .toList();
    }

    /**
     * 페이지별로 기여자 가져오기
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<ContributorResponseDto> getAllContributors(Pageable pageable) {
        Page<Contributor> contributorPage = contributorRepositroy.findAll(pageable);
        return contributorPage.map(ContributorMapper::toResponse);
    }

    /**
     * 기여자 삭제
     *
     * @param contributorId
     */
    @Transactional
    @Override
    public void deleteContributor(Long contributorId) {
        Contributor contributor = this.findById(contributorId);
        contributorRepositroy.delete(contributor);
    }


    /**
     * 기여자 단건 조회
     *
     * @param contributorId
     * @return
     */
    @Override
    public Contributor findById(Long contributorId) {
        Optional<Contributor> optionalContributor = contributorRepositroy.findById(contributorId);
        if (optionalContributor.isPresent()) {
            return optionalContributor.get();
        } else {
            throw new ContributorNotFoundException("찾는 기여자가없습니다");
        }
    }
}
