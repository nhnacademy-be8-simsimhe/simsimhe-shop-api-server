package com.simsimbookstore.apiserver.books.category.service;

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
    public Page<CategoryResponseDto> getAllCategoryPage(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(CategoryMapper::toResponse);
    }
}
