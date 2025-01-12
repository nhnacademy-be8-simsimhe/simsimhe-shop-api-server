package com.simsimbookstore.apiserver.books.category.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.mapper.CategoryMapper;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Transactional
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        //부모 카테고리 조회
        Category parentCategory = null;
        if (categoryRequestDto.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryRequestDto.getParentId())
                    .orElseThrow(() -> new NotFoundException("부모 카테고리를 찾을 수 없습니다"));
        }
        // 새로운 카테고리 엔티티 생성
        Category category = CategoryMapper.toEntity(categoryRequestDto, parentCategory);
        //저장
        Category saveCategory = categoryRepository.save(category);

        return CategoryMapper.toResponse(saveCategory);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAllOrderedById();
        return CategoryMapper.toResponseList(categories);
    }


    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("카테고리를 찾을 수 없습니다"));

        return CategoryMapper.toResponse(category);

    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다"));

        // 자식 카테고리를 데이터베이스에서 조회
        List<Category> children = categoryRepository.findAllByParent(category);

        if (!children.isEmpty()) {
            throw new NotFoundException("자식 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        // 삭제
        categoryRepository.delete(category);
    }


    @Override
    public PageResponse<CategoryResponseDto> getAllCategoryPage(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        // Convert Category to CategoryResponseDto
        List<CategoryResponseDto> responses = categoryPage.getContent().stream()
                .map(CategoryMapper::toResponse)
                .collect(Collectors.toList());

        // Define pagination details
        int maxPageButtons = 5;
        int startPage = Math.max(1, categoryPage.getNumber() + 1 - (maxPageButtons / 2)); // Adjust for 1-based indexing
        int endPage = Math.min(startPage + maxPageButtons - 1, categoryPage.getTotalPages());

        if (endPage - startPage + 1 < maxPageButtons) {
            startPage = Math.max(1, endPage - maxPageButtons + 1);
        }

        // Build and return PageResponse
        return PageResponse.<CategoryResponseDto>builder()
                .data(responses)
                .currentPage(pageable.getPageNumber()) // Convert to 1-based indexing
                .startPage(startPage)
                .endPage(endPage)
                .totalPage(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .build();
    }

}
