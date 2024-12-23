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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @DisplayName("DB에 등록되어 있지 않은 태그 등록하기")
    void saveNewTag() {
        // Arrange
        TagRequestDto requestDto = new TagRequestDto("외국도서");
        Tag savedTag = Tag.builder().tagId(2L).tagName("외국도서").build();

        when(tagRepository.findByTagName("외국도서")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        // Act
        TagResponseDto responseDto = tagService.createTag(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("외국도서", responseDto.getTagName());
        verify(tagRepository, times(1)).findByTagName("외국도서");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("DB에 등록되어 있는 태그 등록하면 이미 존재하는 태그 반환")
    void savePresentTag() {
        // Arrange
        when(tagRepository.findByTagName("국내도서")).thenReturn(Optional.of(mockTag));

        // Act
        TagResponseDto responseDto = tagService.createTag(new TagRequestDto("국내도서"));

        // Assert
        assertNotNull(responseDto);
        assertEquals("국내도서", responseDto.getTagName());
        verify(tagRepository, times(1)).findByTagName("국내도서");
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 삭제하기")
    void deleteTag() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(mockTag));
        doNothing().when(tagRepository).delete(mockTag);

        // Act
        tagService.deleteTag(1L);

        // Assert
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).delete(mockTag);
    }

    @Test
    @DisplayName("저장되어 있는 모든 태그 가져오기")
    void getAllTag() {
        // Arrange
        Tag secondTag = Tag.builder().tagId(2L).tagName("외국도서").build();
        when(tagRepository.findAllTags()).thenReturn(Arrays.asList(mockTag, secondTag));

        // Act
        List<TagResponseDto> result = tagService.getAlltag();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("국내도서", result.get(0).getTagName());
        assertEquals("외국도서", result.get(1).getTagName());
        verify(tagRepository, times(1)).findAllTags();
    }


    @Test
    @DisplayName("저장되어 있는 태그 하나 가져오기")
    void findById() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(mockTag));

        // Act
        Tag tag = tagService.getTag(1L);

        // Assert
        assertNotNull(tag);
        assertEquals("국내도서", tag.getTagName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("찾는 태그가 없으면 NotFoundException")
    void findById_NotFound() {
        // Arrange
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> tagService.getTag(999L));

        verify(tagRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("페이징 처리된 태그 목록 조회")
    void getAllTags() {
        // Arrange
        Tag secondTag = Tag.builder().tagId(2L).tagName("외국도서").build();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Tag> mockPage = new PageImpl<>(Arrays.asList(mockTag, secondTag), pageable, 2);

        when(tagRepository.findAll(pageable)).thenReturn(mockPage);

        // Act
        Page<TagResponseDto> tagResponsePage = tagService.getAllTags(pageable);

        // Assert
        assertNotNull(tagResponsePage);
        assertEquals(2, tagResponsePage.getContent().size());
        assertEquals("국내도서", tagResponsePage.getContent().get(0).getTagName());
        assertEquals("외국도서", tagResponsePage.getContent().get(1).getTagName());
        verify(tagRepository, times(1)).findAll(pageable);
    }
}
