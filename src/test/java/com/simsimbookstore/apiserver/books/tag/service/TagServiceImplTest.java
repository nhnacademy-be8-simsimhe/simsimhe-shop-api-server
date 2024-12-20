package com.simsimbookstore.apiserver.books.tag.service;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.exception.TagNotFoundException;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
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
class TagServiceImplTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void init() {
        // 초기 데이터 추가
        TagRequestDto requestDto = new TagRequestDto("국내도서");
        tagService.saveTag(requestDto);
    }


    @Test
    @DisplayName("DB에 등록되어 있지 않은 태그등록하기")
    void saveNewTag() {
        TagRequestDto requestDto = new TagRequestDto("외국도서");


        TagResponseDto responseDto = tagService.saveTag(requestDto);

        assertNotNull(responseDto);
        assertEquals("외국도서", responseDto.getTagName());
        assertTrue(tagRepository.findByTagName("외국도서").isPresent());
    }

    @Test
    @DisplayName("DB에 등록되어 있는 태그 등록하면 이미 존재하는 태그 반환")
    void savePresentTag() {
        TagRequestDto requestDto = new TagRequestDto("국내도서");

        TagResponseDto responseDto = tagService.saveTag(requestDto);
        assertNotNull(responseDto);
        assertEquals("국내도서", responseDto.getTagName());
        assertTrue(tagRepository.findByTagName("국내도서").isPresent());
    }

    @Test
    @DisplayName("태그 삭제하기")
    void deleteTag() {
        // Arrange: 초기 데이터 저장
        TagRequestDto requestDto = new TagRequestDto("DVD");
        TagResponseDto savedTag = tagService.saveTag(requestDto); // 태그 저장 후 DTO 반환
        Long tagId = savedTag.getTagId(); // 저장된 태그 ID 가져오기

        // Act: 태그 삭제
        tagService.deleteTag(tagId);

        // Assert: 삭제 검증
        assertFalse(tagRepository.findById(tagId).isPresent()); // 해당 태그가 삭제되었는지 확인
    }

    @Test
    @DisplayName("저장되어 있는 모든 태그 가져오기")
    void getAllTag() {
        TagRequestDto requestDto = new TagRequestDto("DVD");
        TagResponseDto savedTag = tagService.saveTag(requestDto); // 태그 저장 후 DTO 반환

        List<TagResponseDto> result = tagService.getAlltag();

        assertNotNull(result);
        //임의로 넣어논 데이터가 있기때문에 2개
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("저장되어있는 태그 하나 가져오기")
    void findById() {
        TagRequestDto requestDto = new TagRequestDto("DVD");
        TagResponseDto savedTag = tagService.saveTag(requestDto); // 태그 저장 후 DTO 반환

        Tag tag = tagService.findById(savedTag.getTagId());

        assertEquals(tag.getTagName(), requestDto.getTagName());
        assertEquals(tag.getTagName(), savedTag.getTagName());
    }

    @Test
    @DisplayName("찾는 태그가 없으면 TagNotFoundException")
    void findById_NotFound() {
        // Act & Assert
        assertThrows(TagNotFoundException.class,
                () -> tagService.findById(999L)
        );
    }

    @Test
    @DisplayName("페이징 처리된 태그 목록 조회")
    void getAllTags() {
        // Arrange: 테스트 데이터를 저장
        TagRequestDto requestDto1 = new TagRequestDto("DVD");
        TagRequestDto requestDto2 = new TagRequestDto("음반");
        TagRequestDto requestDto3 = new TagRequestDto("외국도서");

        tagService.saveTag(requestDto1);
        tagService.saveTag(requestDto2);
        tagService.saveTag(requestDto3);

        // Page 요청 설정 (첫 번째 페이지, 2개씩 가져오기)
        Pageable pageable = PageRequest.of(0, 2);

        // Act: 첫 번째 페이지 데이터 가져오기
        Page<TagResponseDto> tagResponsePage = tagService.getAllTags(pageable);

        // Assert: 첫 번째 페이지 검증
        assertNotNull(tagResponsePage);
        assertEquals(2, tagResponsePage.getContent().size()); // 한 페이지에 2개 데이터
        assertEquals(2, tagResponsePage.getTotalPages()); // 총 2페이지
        assertEquals(4, tagResponsePage.getTotalElements()); // 총 4개 데이터
        assertEquals("국내도서", tagResponsePage.getContent().get(0).getTagName()); // 첫 번째 페이지의 첫 번째 태그
        assertEquals("DVD", tagResponsePage.getContent().get(1).getTagName()); // 첫 번째 페이지의 두 번째 태그

        // Page 요청 설정 (두 번째 페이지, 2개씩 가져오기)
        pageable = PageRequest.of(1, 2);

        // Act: 두 번째 페이지 데이터 가져오기
        tagResponsePage = tagService.getAllTags(pageable);

        // Assert: 두 번째 페이지 검증
        assertNotNull(tagResponsePage);
        assertEquals(2, tagResponsePage.getContent().size()); // 한 페이지에 2개 데이터
        assertEquals(2, tagResponsePage.getTotalPages()); // 총 2페이지
        assertEquals("음반", tagResponsePage.getContent().get(0).getTagName()); // 두 번째 페이지의 첫 번째 태그
        assertEquals("외국도서", tagResponsePage.getContent().get(1).getTagName()); // 두 번째 페이지의 두 번째 태그
    }


}