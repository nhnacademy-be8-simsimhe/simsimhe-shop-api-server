package com.simsimbookstore.apiserver.books.category.service;

import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.exception.CategoryNotFoundException;
import com.simsimbookstore.apiserver.books.category.mapper.CategoryMapper;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public CategoryResponseDto saveCategory(CategoryRequestDto categoryRequestDto) {
        //부모 카테고리 조회
        Category parentCategory = null;
        if (categoryRequestDto.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryRequestDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("부모 카테고리를 찾을 수 없습니다"));
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
                () -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다"));

        return CategoryMapper.toResponse(category);

    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다"));

        // 자식 카테고리가 있는 경우 예외 발생
        if (!category.getChildren().isEmpty()) {
            throw new RuntimeException("자식 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        // 삭제
        categoryRepository.delete(category);

    }

    @Override
    public Page<CategoryResponseDto> getAllCategoryPage(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(CategoryMapper::toResponse);
    }
}
