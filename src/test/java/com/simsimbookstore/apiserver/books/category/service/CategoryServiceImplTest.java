package com.simsimbookstore.apiserver.books.category.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category mockParentCategory;
    private Category mockChildCategory;

    @BeforeEach
    void setUp() {

        mockParentCategory = Category.builder()
                .categoryId(1L)
                .categoryName("도서")
                .build();

        mockChildCategory = Category.builder()
                .categoryId(2L)
                .categoryName("소설")
                .parent(mockParentCategory)
                .build();
    }

    @Test
    @DisplayName("새로운 카테고리 저장")
    void saveCategory() {
        // Arrange
        CategoryRequestDto requestDto = new CategoryRequestDto("도서", null);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setCategoryId(1L);
            return category;
        });

        // Act
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("도서", responseDto.getCategoryName());
        assertNull(responseDto.getParentId());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("부모 카테고리를 가진 카테고리 저장")
    void saveCategoryWithParent() {
        // Arrange
        when(categoryRepository.findById(mockParentCategory.getCategoryId()))
                .thenReturn(Optional.of(mockParentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setCategoryId(2L);
            return category;
        });

        CategoryRequestDto requestDto = new CategoryRequestDto("소설", mockParentCategory.getCategoryId());

        // Act
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("소설", responseDto.getCategoryName());
        assertEquals(mockParentCategory.getCategoryId(), responseDto.getParentId());

        verify(categoryRepository, times(1)).findById(mockParentCategory.getCategoryId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 단건 조회")
    void getCategoryById() {
        // Arrange
        when(categoryRepository.findById(mockParentCategory.getCategoryId()))
                .thenReturn(Optional.of(mockParentCategory));

        // Act
        CategoryResponseDto responseDto = categoryService.getCategoryById(mockParentCategory.getCategoryId());

        // Assert
        assertNotNull(responseDto);
        assertEquals("도서", responseDto.getCategoryName());

        verify(categoryRepository, times(1)).findById(mockParentCategory.getCategoryId());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 조회 시 예외 발생")
    void getCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(999L));

        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("자식 카테고리가 있는 경우 삭제 시 예외 발생")
    void deleteCategoryWithChildren() {
        // Arrange
        when(categoryRepository.findById(mockParentCategory.getCategoryId()))
                .thenReturn(Optional.of(mockParentCategory));

        // 자식 카테고리를 반환하도록 설정
        when(categoryRepository.findAllByParent(mockParentCategory))
                .thenReturn(List.of(mockChildCategory));

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> categoryService.deleteCategory(mockParentCategory.getCategoryId()));

        verify(categoryRepository, times(1)).findById(mockParentCategory.getCategoryId());
        verify(categoryRepository, times(1)).findAllByParent(mockParentCategory);
    }


    @Test
    @DisplayName("자식 카테고리가 없는 경우 삭제 성공")
    void deleteCategory() {
        // Arrange
        when(categoryRepository.findById(mockParentCategory.getCategoryId()))
                .thenReturn(Optional.of(mockParentCategory));

        when(categoryRepository.findAllByParent(mockParentCategory))
                .thenReturn(List.of());

        doNothing().when(categoryRepository).delete(mockParentCategory);

        // Act
        categoryService.deleteCategory(mockParentCategory.getCategoryId());

        // Assert
        verify(categoryRepository, times(1)).delete(mockParentCategory);
    }

    @Test
    @DisplayName("모든 카테고리 조회")
    void getAllCategories() {
        // Arrange
        when(categoryRepository.findAllOrderedById())
                .thenReturn(Arrays.asList(mockParentCategory, mockChildCategory));

        // Act
        List<CategoryResponseDto> categories = categoryService.getAllCategories();

        // Assert
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("도서", categories.get(0).getCategoryName());
        assertEquals("소설", categories.get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAllOrderedById();
    }

    @Test
    @DisplayName("페이징 처리된 카테고리 목록 조회")
    void getAllCategoryPage() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 2);

        // Page<Category> Mock 생성
        Page<Category> mockPage = new PageImpl<>(Arrays.asList(mockParentCategory, mockChildCategory), pageable, 2);

        when(categoryRepository.findAll(pageable)).thenReturn(mockPage);

        // Act
        PageResponse<CategoryResponseDto> categoryPage = categoryService.getAllCategoryPage(pageable);

        // Assert
        assertNotNull(categoryPage);
        assertEquals(2, categoryPage.getData().size()); // 한 페이지에 2개
        assertEquals(1, categoryPage.getTotalPage()); // 총 1페이지
        assertEquals(2, categoryPage.getTotalElements()); // 총 2개 데이터
        assertEquals("도서", categoryPage.getData().getFirst().getCategoryName());
        assertEquals("소설", categoryPage.getData().get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAll(pageable);
    }


}
