package com.simsimbookstore.apiserver.books.category.mapper;

import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    /**
     * CategoryRequestDto를 Category 엔티티로 변환
     *
     * @param categoryRequestDto 요청 DTO
     * @param parentCategory     부모 카테고리 (없을 경우 null)
     * @return Category 엔티티
     */
    public static Category toEntity(CategoryRequestDto categoryRequestDto, Category parentCategory) {
        return Category.builder()
                .categoryName(categoryRequestDto.getCategoryName())
                .parent(parentCategory)
                .build();
    }

    /**
     * Category 엔티티를 CategoryResponseDto로 변환
     *
     * @param category Category 엔티티
     * @return CategoryResponseDto
     */
    public static CategoryResponseDto toResponse(Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .parentName(category.getParent() != null ? category.getParent().getCategoryName() : null)
                .children(toResponseList(category.getChildren()))
                .build();
    }

    /**
     * Category 엔티티 리스트를 CategoryResponseDto 리스트로 변환
     *
     * @param categories Category 엔티티 리스트
     * @return CategoryResponseDto 리스트
     */
    public static List<CategoryResponseDto> toResponseList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories.stream()
                .map(CategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

}