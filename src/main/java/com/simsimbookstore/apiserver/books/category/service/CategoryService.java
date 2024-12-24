package com.simsimbookstore.apiserver.books.category.service;

import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);

    Page<CategoryResponseDto> getAllCategoryPage(Pageable pageable);
}
