package com.simsimbookstore.apiserver.books.category.service;

import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.exception.CategoryNotFoundException;
import com.simsimbookstore.apiserver.books.category.exception.ChildCategoryExistException;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
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
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 카테고리 저장")
    void saveCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto("국내도서", null);

        CategoryResponseDto responseDto = categoryService.saveCategory(requestDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals("국내도서", responseDto.getCategoryName());
        Assertions.assertEquals(null, responseDto.getParentId());
    }

    @Test
    @DisplayName("부모 카테고리를 가진 카테고리 저장")
    void saveCategoryWithParent() {

        CategoryRequestDto parentDto = new CategoryRequestDto("국내도서", null);

        CategoryResponseDto parentResponse = categoryService.saveCategory(parentDto);

        CategoryRequestDto childDto = new CategoryRequestDto("소설", parentResponse.getCategoryId());

        CategoryResponseDto childResponse = categoryService.saveCategory(childDto);

        Assertions.assertNotNull(childResponse);
        Assertions.assertEquals("소설", childResponse.getCategoryName());
        Assertions.assertEquals(parentResponse.getCategoryId(), childResponse.getParentId());
    }

    @Test
    @DisplayName("모든 카테고리 조회")
    void getAllCategories() {
        // given
        CategoryResponseDto parentCategory = categoryService.saveCategory(new CategoryRequestDto("국내도서", null));
        categoryService.saveCategory(new CategoryRequestDto("전자제품", parentCategory.getCategoryId()));

        // when
        List<CategoryResponseDto> categories = categoryService.getAllCategories();

        // then
        assertNotNull(categories);
        assertEquals(2, categories.size());
    }


    @Test
    @DisplayName("카테고리 단건 조회")
    void getCategoryById() {
        // given
        CategoryRequestDto requestDto = new CategoryRequestDto("국내도서", null);
        CategoryResponseDto responseDto = categoryService.saveCategory(requestDto);

        // when
        CategoryResponseDto foundCategory = categoryService.getCategoryById(responseDto.getCategoryId());

        // then
        assertNotNull(foundCategory);
        assertEquals("국내도서", foundCategory.getCategoryName());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 조회 시 예외 발생")
    void getCategoryById_NotFound() {
        // expect
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(999L));
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() {
        // given
        CategoryRequestDto requestDto = new CategoryRequestDto("도서", null);
        CategoryResponseDto responseDto = categoryService.saveCategory(requestDto);

        // when
        categoryService.deleteCategory(responseDto.getCategoryId());

        // then
        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(responseDto.getCategoryId()));
    }

    @Test
    @DisplayName("자식 카테고리가 있는 경우 삭제 시 예외 발생")
    void deleteCategoryWithChildren() {
        // given
        CategoryRequestDto parentDto = new CategoryRequestDto("도서", null);
        CategoryResponseDto parentResponse = categoryService.saveCategory(parentDto);

        CategoryRequestDto childDto = new CategoryRequestDto("소설", parentResponse.getCategoryId());
        categoryService.saveCategory(childDto);

        // when & then
        Assertions.assertThrows(ChildCategoryExistException.class,
                () -> categoryService.deleteCategory(parentResponse.getCategoryId()));
    }

    @Test
    @DisplayName("페이징 처리된 카테고리 목록 조회")
    void getAllCategoryPage() {
        // given
        categoryService.saveCategory(new CategoryRequestDto("도서", null));
        categoryService.saveCategory(new CategoryRequestDto("전자제품", null));
        categoryService.saveCategory(new CategoryRequestDto("가구", null));

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<CategoryResponseDto> categoryPage = categoryService.getAllCategoryPage(pageable);

        // then
        assertNotNull(categoryPage);
        assertEquals(2, categoryPage.getContent().size()); // 한 페이지에 2개
        assertEquals(2, categoryPage.getTotalPages()); // 총 2페이지
        assertEquals(3, categoryPage.getTotalElements()); // 총 3개 데이터
    }

}