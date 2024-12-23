//package com.simsimbookstore.apiserver.books.contributor.service;
//
//import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
//import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
//import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
//import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
//import com.simsimbookstore.apiserver.exception.NotFoundException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@SpringBootTest
//@Transactional
//class ContributorServiceImplTest {
//
//    @Autowired
//    ContributorService contributorService;
//
//    @Autowired
//    ContributorRepositroy contributorRepositroy;
//
//    @BeforeEach
//    void init() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("유선경", "지은이");
//        contributorService.createContributor(requestDto);
//    }
//
//    @BeforeEach
//    void cleanDatabase() {
//        contributorRepositroy.deleteAll();
//    }
//
//
//    @Test
//    @DisplayName("DB에 등록되어 있지 않은 기여자 등록하기")
//    void saveNewContribitor() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("김영한", "지은이");
//
//        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
//
//        Assertions.assertNotNull(responseDto);
//        Assertions.assertEquals("김영한", responseDto.getContributorName());
//        Assertions.assertEquals("지은이", responseDto.getContributorRole());
//        Assertions.assertTrue(contributorRepositroy.findByContributorName(responseDto.getContributorName()).isPresent());
//    }
//
//    @Test
//    @DisplayName("DB에 이미 존재하는 기여자는 등록되어있는 데이터 반환")
//    void savePresentContributor() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");
//
//        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
//
//        Assertions.assertEquals("한강", responseDto.getContributorName());
//        Assertions.assertEquals("지은이", responseDto.getContributorRole());
//        Assertions.assertTrue(contributorRepositroy.findByContributorName(responseDto.getContributorName()).isPresent());
//    }
//
//    @Test
//    @DisplayName("기여자 삭제")
//    void deleteContributor() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");
//
//        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
//
//        Long contributorId = responseDto.getContributorId();
//
//        contributorService.deleteContributor(contributorId);
//
//        Assertions.assertFalse(contributorRepositroy.findById(contributorId).isPresent());
//    }
//
//    @Test
//    @DisplayName("저장 되어 있는 모든 기여자 조회")
//    void getAllContributor() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");
//
//        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
//
//        List<ContributorResponseDto> result = contributorService.getAllContributor();
//
//        Assertions.assertNotNull(result);
//
//        //초기데이터도 있어서 2개
//        Assertions.assertEquals(2, result.size());
//        Assertions.assertEquals("한강", result.get(1).getContributorName());
//        Assertions.assertEquals("지은이", result.get(1).getContributorRole());
//    }
//
//    @Test
//    @DisplayName("기여자 목록이 비어 있을 경우")
//    void getAllContributor_Empty() {
//        contributorRepositroy.deleteAll();
//        List<ContributorResponseDto> result = contributorService.getAllContributor();
//
//        Assertions.assertNotNull(result);
//        Assertions.assertTrue(result.isEmpty());
//    }
//
//
//    @Test
//    @DisplayName("기여자 단건 조회")
//    void findById() {
//        ContributorRequestDto requestDto = new ContributorRequestDto("한강", "지은이");
//
//        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
//
//        Contributor contributor = contributorService.getContributer(responseDto.getContributorId());
//
//        Assertions.assertEquals(contributor.getContributorName(), requestDto.getContributorName());
//        Assertions.assertEquals(contributor.getContributorName(), responseDto.getContributorName());
//    }
//
//    @Test
//    @DisplayName("찾는 기여자가 없으면 에러")
//    void findById_NotFound() {
//        Assertions.assertThrows(NotFoundException.class,
//                () -> contributorService.getContributer(999L)
//        );
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 기여자 삭제 시 예외 발생")
//    void deleteContributor_NotFound() {
//        Assertions.assertThrows(NotFoundException.class,
//                () -> contributorService.deleteContributor(999L));
//    }
//
//
//    @Test
//    @DisplayName("페이징 처리된 기여자 목록조회")
//    void getAllContributors() {
//        ContributorRequestDto requestDto1 = new ContributorRequestDto("한강", "지은이");
//        ContributorRequestDto requestDto2 = new ContributorRequestDto("임채환", "옮긴이");
//        ContributorRequestDto requestDto3 = new ContributorRequestDto("박신지", "그림");
//
//        contributorService.createContributor(requestDto1);
//        contributorService.createContributor(requestDto2);
//        contributorService.createContributor(requestDto3);
//
//        Pageable pageable = PageRequest.of(0, 2);
//
//        Page<ContributorResponseDto> contributorResponseDtoPage = contributorService.getAllContributors(pageable);
//
//        Assertions.assertNotNull(contributorResponseDtoPage);
//        Assertions.assertEquals(2, contributorResponseDtoPage.getContent().size()); // 한 페이지에 2개 데이터
//        Assertions.assertEquals(2, contributorResponseDtoPage.getTotalPages()); // 총 2페이지
//        Assertions.assertEquals(4, contributorResponseDtoPage.getTotalElements()); // 총 4개 데이터
//        Assertions.assertEquals("유선경", contributorResponseDtoPage.getContent().get(0).getContributorName());
//        Assertions.assertEquals("한강", contributorResponseDtoPage.getContent().get(1).getContributorName());
//
//
//        //두번째 페이지
//        pageable = PageRequest.of(1, 2);
//        contributorResponseDtoPage = contributorService.getAllContributors(pageable);
//        Assertions.assertEquals(2, contributorResponseDtoPage.getContent().size()); // 한 페이지에 2개 데이터
//        Assertions.assertEquals(2, contributorResponseDtoPage.getTotalPages()); // 총 2페이지
//        Assertions.assertEquals("임채환", contributorResponseDtoPage.getContent().get(0).getContributorName());
//        Assertions.assertEquals("박신지", contributorResponseDtoPage.getContent().get(1).getContributorName());
//
//
//    }
//
//    @Test
//    @DisplayName("기여자 수정")
//    void update() {
//        // 데이터 생성
//        ContributorRequestDto saveDto = new ContributorRequestDto("임시 데이터", "저자");
//        ContributorResponseDto savedContributor = contributorService.createContributor(saveDto);
//
//        // ID 가져오기
//        Long contributorId = savedContributor.getContributorId();
//
//        // 수정 요청
//        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");
//        ContributorResponseDto updatedResponse = contributorService.updateContributor(contributorId, updateDto);
//
//        // 검증
//        Assertions.assertNotNull(updatedResponse);
//        Assertions.assertEquals("임채환", updatedResponse.getContributorName());
//        Assertions.assertEquals("옮긴이", updatedResponse.getContributorRole());
//    }
//
//
//    @Test
//    @DisplayName("존재하지 않는 기여자 수정 시 예외 발생")
//    void updateContributor_NotFound() {
//        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");
//
//        Assertions.assertThrows(NotFoundException.class,
//                () -> contributorService.updateContributor(999L, updateDto));
//    }
//
//
//}

package com.simsimbookstore.apiserver.books.contributor.service;

import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContributorServiceImplTest {

    @InjectMocks
    private ContributorServiceImpl contributorService;

    @Mock
    private ContributorRepositroy contributorRepository;

    private Contributor mockContributor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockContributor = Contributor.builder()
                .contributorId(1L)
                .contributorName("유선경")
                .contributorRole("지은이")
                .build();
    }

    @Test
    @DisplayName("DB에 등록되어 있지 않은 기여자 등록하기")
    void saveNewContributor() {
        // Arrange
        ContributorRequestDto requestDto = new ContributorRequestDto("김영한", "지은이");
        Contributor savedContributor = Contributor.builder()
                .contributorId(2L)
                .contributorName("김영한")
                .contributorRole("지은이")
                .build();

        when(contributorRepository.findByContributorName("김영한"))
                .thenReturn(Optional.empty());
        when(contributorRepository.save(any(Contributor.class)))
                .thenReturn(savedContributor);

        // Act
        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("김영한", responseDto.getContributorName());
        assertEquals("지은이", responseDto.getContributorRole());
        verify(contributorRepository, times(1)).findByContributorName("김영한");
        verify(contributorRepository, times(1)).save(any(Contributor.class));
    }

    @Test
    @DisplayName("DB에 이미 존재하는 기여자는 등록되어있는 데이터 반환")
    void savePresentContributor() {
        // Arrange
        when(contributorRepository.findByContributorName("유선경"))
                .thenReturn(Optional.of(mockContributor));

        // Act
        ContributorResponseDto responseDto = contributorService.createContributor(new ContributorRequestDto("유선경", "지은이"));

        // Assert
        assertNotNull(responseDto);
        assertEquals("유선경", responseDto.getContributorName());
        assertEquals("지은이", responseDto.getContributorRole());
        verify(contributorRepository, times(1)).findByContributorName("유선경");
        verify(contributorRepository, never()).save(any(Contributor.class));
    }

    @Test
    @DisplayName("기여자 삭제")
    void deleteContributor() {
        // Arrange
        when(contributorRepository.findById(1L))
                .thenReturn(Optional.of(mockContributor));
        doNothing().when(contributorRepository).delete(mockContributor);

        // Act
        contributorService.deleteContributor(1L);

        // Assert
        verify(contributorRepository, times(1)).findById(1L);
        verify(contributorRepository, times(1)).delete(mockContributor);
    }

    @Test
    @DisplayName("저장되어 있는 모든 기여자 조회")
    void getAllContributor() {
        // Arrange
        Contributor secondContributor = Contributor.builder()
                .contributorId(2L)
                .contributorName("한강")
                .contributorRole("지은이")
                .build();

        when(contributorRepository.findAllContributors()).thenReturn(Arrays.asList(mockContributor, secondContributor));

        // Act
        List<ContributorResponseDto> result = contributorService.getAllContributor();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("유선경", result.get(0).getContributorName());
        assertEquals("한강", result.get(1).getContributorName());
        verify(contributorRepository, times(1)).findAllContributors();
    }


    @Test
    @DisplayName("기여자 단건 조회")
    void findById() {
        // Arrange
        when(contributorRepository.findById(1L))
                .thenReturn(Optional.of(mockContributor));

        // Act
        Contributor contributor = contributorService.getContributer(1L);

        // Assert
        assertNotNull(contributor);
        assertEquals("유선경", contributor.getContributorName());
        verify(contributorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("찾는 기여자가 없으면 예외 발생")
    void findById_NotFound() {
        // Arrange
        when(contributorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> contributorService.getContributer(999L));
        verify(contributorRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("페이징 처리된 기여자 목록 조회")
    void getAllContributors() {
        // Arrange
        Contributor secondContributor = Contributor.builder()
                .contributorId(2L)
                .contributorName("한강")
                .contributorRole("지은이")
                .build();

        Pageable pageable = PageRequest.of(0, 2);
        Page<Contributor> mockPage = new PageImpl<>(Arrays.asList(mockContributor, secondContributor), pageable, 2);

        when(contributorRepository.findAll(pageable)).thenReturn(mockPage);

        // Act
        Page<ContributorResponseDto> contributorResponseDtoPage = contributorService.getAllContributors(pageable);

        // Assert
        assertNotNull(contributorResponseDtoPage);
        assertEquals(2, contributorResponseDtoPage.getContent().size());
        assertEquals("유선경", contributorResponseDtoPage.getContent().get(0).getContributorName());
        assertEquals("한강", contributorResponseDtoPage.getContent().get(1).getContributorName());
        verify(contributorRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("기여자 수정")
    void update() {
        // Arrange
        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");
        Contributor updatedContributor = Contributor.builder()
                .contributorId(1L)
                .contributorName("임채환")
                .contributorRole("옮긴이")
                .build();

        when(contributorRepository.findById(1L)).thenReturn(Optional.of(mockContributor));
        when(contributorRepository.save(any(Contributor.class))).thenReturn(updatedContributor);

        // Act
        ContributorResponseDto responseDto = contributorService.updateContributor(1L, updateDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("임채환", responseDto.getContributorName());
        assertEquals("옮긴이", responseDto.getContributorRole());
        verify(contributorRepository, times(1)).findById(1L);
        verify(contributorRepository, times(1)).save(any(Contributor.class));
    }

    @Test
    @DisplayName("존재하지 않는 기여자 수정 시 예외 발생")
    void updateContributor_NotFound() {
        // Arrange
        ContributorRequestDto updateDto = new ContributorRequestDto("임채환", "옮긴이");

        when(contributorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> contributorService.updateContributor(999L, updateDto));
        verify(contributorRepository, times(1)).findById(999L);
    }
}
