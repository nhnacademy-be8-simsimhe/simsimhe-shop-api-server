package com.simsimbookstore.apiserver.books.tag.service;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    private Tag mockTag;

    @BeforeEach
    void setUp() {
        mockTag = Tag.builder()
                .tagId(1L)
                .tagName("국내도서")
                .build();
    }

    @Test
    @DisplayName("새로운 태그를 등록할 때 성공해야 함")
    void createNewTag() {
        TagRequestDto requestDto = new TagRequestDto("외국도서");
        Tag savedTag = Tag.builder().tagId(2L).tagName("외국도서").build();

        when(tagRepository.findByTagName("외국도서")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        TagResponseDto responseDto = tagService.createTag(requestDto);

        assertNotNull(responseDto);
        assertEquals("외국도서", responseDto.getTagName());
        verify(tagRepository).findByTagName("외국도서");
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    @DisplayName("이미 존재하는 태그 등록 요청시 활성화만 처리")
    void createExistingTag() {
        when(tagRepository.findByTagName("국내도서")).thenReturn(Optional.of(mockTag));

        TagResponseDto responseDto = tagService.createTag(new TagRequestDto("국내도서"));

        assertNotNull(responseDto);
        assertEquals("국내도서", responseDto.getTagName());
        verify(tagRepository).findByTagName("국내도서");
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 삭제 요청 성공")
    void deleteTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(mockTag));

        tagService.deleteTag(1L);

        verify(tagRepository).findById(1L);
        assertFalse(mockTag.isActivated());
    }

    @Test
    @DisplayName("모든 활성화된 태그 조회")
    void getAllTags() {
        Tag secondTag = Tag.builder().tagId(2L).tagName("외국도서").build();
        when(tagRepository.findAllActivated()).thenReturn(Arrays.asList(mockTag, secondTag));

        List<TagResponseDto> result = tagService.getAlltag();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("국내도서", result.get(0).getTagName());
        assertEquals("외국도서", result.get(1).getTagName());
        verify(tagRepository).findAllActivated();
    }

    @Test
    @DisplayName("특정 태그 ID로 조회 성공")
    void findTagById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(mockTag));

        Tag tag = tagService.getTag(1L);

        assertNotNull(tag);
        assertEquals("국내도서", tag.getTagName());
        verify(tagRepository).findById(1L);
    }

    @Test
    @DisplayName("태그 조회 실패시 NotFoundException 던짐")
    void findTagById_NotFound() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagService.getTag(999L));
        verify(tagRepository).findById(999L);
    }



    @Test
    @DisplayName("태그 업데이트 성공")
    void updateTag() {
        TagRequestDto requestDto = new TagRequestDto("업데이트된 태그");
        Tag updatedTag = Tag.builder().tagId(1L).tagName("업데이트된 태그").build();

        when(tagRepository.findById(1L)).thenReturn(Optional.of(mockTag));
        when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        TagResponseDto responseDto = tagService.updateTag(1L, requestDto);

        assertNotNull(responseDto);
        assertEquals("업데이트된 태그", responseDto.getTagName());
        verify(tagRepository).findById(1L);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 업데이트 실패 - 태그를 찾을 수 없음")
    void updateTag_NotFound() {
        TagRequestDto requestDto = new TagRequestDto("업데이트된 태그");
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagService.updateTag(999L, requestDto));
        verify(tagRepository).findById(999L);
        verify(tagRepository, never()).save(any(Tag.class));
    }
}
