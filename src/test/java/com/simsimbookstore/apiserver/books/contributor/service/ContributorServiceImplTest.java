package com.simsimbookstore.apiserver.books.contributor.service;

import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.error.ContributorNotFoundException;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ContributorServiceImplTest {

    @Autowired
    ContributorService contributorService;

    @Autowired
    ContributorRepositroy contributorRepositroy;

    @BeforeEach
    void init() {
        ContributorRequestDto requestDto = new ContributorRequestDto("유선경", "지은이");
        contributorService.saveContributor(requestDto);
    }

    @BeforeEach
    void cleanDatabase() {
        contributorRepositroy.deleteAll();
    }


    @Test
    @DisplayName("DB에 등록되어 있지 않은 기여자 등록하기")
    void saveNewContribitor() {
        ContributorRequestDto requestDto = new ContributorRequestDto("김영한", "지은이");

        ContributorResponseDto responseDto = contributorService.saveContributor(requestDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals("김영한", responseDto.getContributorName());
        Assertions.assertEquals("지은이", responseDto.getContributorRole());
        Assertions.assertTrue(contributorRepositroy.findByContributorName(responseDto.getContributorName()).isPresent());
    }

    @Test
    @DisplayName("DB에 이미 존재하는 기여자는 등록되어있는 데이터 반환")
    void savePresentContributor() {
        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");

        ContributorResponseDto responseDto = contributorService.saveContributor(requestDto);

        Assertions.assertEquals("한강", responseDto.getContributorName());
        Assertions.assertEquals("지은이", responseDto.getContributorRole());
        Assertions.assertTrue(contributorRepositroy.findByContributorName(responseDto.getContributorName()).isPresent());
    }

    @Test
    @DisplayName("기여자 삭제")
    void deleteContributor() {
        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");

        ContributorResponseDto responseDto = contributorService.saveContributor(requestDto);

        Long contributorId = responseDto.getContributorId();

        contributorService.deleteContributor(contributorId);

        Assertions.assertFalse(contributorRepositroy.findById(contributorId).isPresent());
    }

    @Test
    @DisplayName("저장 되어 있는 모든 기여자 조회")
    void getAllContributor() {
        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");

        ContributorResponseDto responseDto = contributorService.saveContributor(requestDto);

        List<ContributorResponseDto> result = contributorService.getAllContributor();

        Assertions.assertNotNull(result);

        //초기데이터도 있어서 2개
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("한강", result.get(1).getContributorName());
        Assertions.assertEquals("지은이", result.get(1).getContributorRole());
    }

    @Test
    @DisplayName("기여자 목록이 비어 있을 경우")
    void getAllContributor_Empty() {
        contributorRepositroy.deleteAll();
        List<ContributorResponseDto> result = contributorService.getAllContributor();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("기여자 단건 조회")
    void findById() {
        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");

        ContributorResponseDto responseDto = contributorService.saveContributor(requestDto);

        Contributor contributor = contributorService.findById(responseDto.getContributorId());

        Assertions.assertEquals(contributor.getContributorName(), requestDto.getContributorName());
        Assertions.assertEquals(contributor.getContributorName(), responseDto.getContributorName());
    }

    @Test
    @DisplayName("찾는 기여자가 없으면 에러")
    void findById_NotFound() {
        Assertions.assertThrows(ContributorNotFoundException.class,
                () -> contributorService.findById(999L)
        );
    }

    @Test
    @DisplayName("존재하지 않는 기여자 삭제 시 예외 발생")
    void deleteContributor_NotFound() {
        Assertions.assertThrows(ContributorNotFoundException.class,
                () -> contributorService.deleteContributor(999L));
    }


    @Test
    @DisplayName("페이징 처리된 기여자 목록조회")
    void getAllContributors() {
        ContributorRequestDto requestDto1 = new ContributorRequestDto("한강", "지은이");
        ContributorRequestDto requestDto2 = new ContributorRequestDto("임채환", "옮긴이");
        ContributorRequestDto requestDto3 = new ContributorRequestDto("박신지", "그림");

        contributorService.saveContributor(requestDto1);
        contributorService.saveContributor(requestDto2);
        contributorService.saveContributor(requestDto3);

        Pageable pageable = PageRequest.of(0, 2);

        Page<ContributorResponseDto> contributorResponseDtoPage = contributorService.getAllContributors(pageable);

        Assertions.assertNotNull(contributorResponseDtoPage);
        Assertions.assertEquals(2, contributorResponseDtoPage.getContent().size()); // 한 페이지에 2개 데이터
        Assertions.assertEquals(2, contributorResponseDtoPage.getTotalPages()); // 총 2페이지
        Assertions.assertEquals(4, contributorResponseDtoPage.getTotalElements()); // 총 4개 데이터
        Assertions.assertEquals("유선경", contributorResponseDtoPage.getContent().get(0).getContributorName());
        Assertions.assertEquals("한강", contributorResponseDtoPage.getContent().get(1).getContributorName());


        //두번째 페이지
        pageable = PageRequest.of(1, 2);
        contributorResponseDtoPage = contributorService.getAllContributors(pageable);
        Assertions.assertEquals(2, contributorResponseDtoPage.getContent().size()); // 한 페이지에 2개 데이터
        Assertions.assertEquals(2, contributorResponseDtoPage.getTotalPages()); // 총 2페이지
        Assertions.assertEquals("임채환", contributorResponseDtoPage.getContent().get(0).getContributorName());
        Assertions.assertEquals("박신지", contributorResponseDtoPage.getContent().get(1).getContributorName());


    }

    @Test
    @DisplayName("기여자 수정")
    void update() {
        // 데이터 생성
        ContributorRequestDto saveDto = new ContributorRequestDto("임시 데이터", "저자");
        ContributorResponseDto savedContributor = contributorService.saveContributor(saveDto);

        // ID 가져오기
        Long contributorId = savedContributor.getContributorId();

        // 수정 요청
        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");
        ContributorResponseDto updatedResponse = contributorService.updateContributor(contributorId, updateDto);

        // 검증
        Assertions.assertNotNull(updatedResponse);
        Assertions.assertEquals("임채환", updatedResponse.getContributorName());
        Assertions.assertEquals("옮긴이", updatedResponse.getContributorRole());
    }


    @Test
    @DisplayName("존재하지 않는 기여자 수정 시 예외 발생")
    void updateContributor_NotFound() {
        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");

        Assertions.assertThrows(ContributorNotFoundException.class,
                () -> contributorService.updateContributor(999L, updateDto));
    }


}