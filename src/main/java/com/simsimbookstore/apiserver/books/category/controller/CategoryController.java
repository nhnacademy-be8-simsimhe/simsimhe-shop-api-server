package com.simsimbookstore.apiserver.books.category.controller;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class CategoryController {

    private final CategoryService categoryService;


    /**
     * 카테고리 등록
     *
     * @param requestDto 요청 DTO
     * @return 등록된 카테고리 정보
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> saveCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 모든 카테고리 조회
     *
     * @return 전체 카테고리 목록
     */
    @GetMapping("/categories/list")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 페이지 별로 카테고리조회
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/categories")
    public ResponseEntity<PageResponse<CategoryResponseDto>> getAllCategoryPage(@RequestParam(defaultValue = "1") int page,
                                                                                @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("categoryId").ascending());
        PageResponse<CategoryResponseDto> response = categoryService.getAllCategoryPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 단일 카테고리 조회
     *
     * @param categoryId 카테고리 ID
     * @return 조회된 카테고리 정보
     */
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long categoryId) {

        CategoryResponseDto responseDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(responseDto);

    }

    /**
     * 카테고리 삭제
     *
     * @param categoryId 카테고리 ID
     * @return 상태 응답
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


}
