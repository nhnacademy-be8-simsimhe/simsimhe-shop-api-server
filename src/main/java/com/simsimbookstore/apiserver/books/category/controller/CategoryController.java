package com.simsimbookstore.apiserver.books.category.controller;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.service.CategoryService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
     * @param requestDto    요청 DTO
     * @param bindingResult 검증 결과
     * @return 등록된 카테고리 정보
     */
    @PostMapping("/categories")
    public ResponseEntity<?> saveCategory(@RequestBody @Valid CategoryRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        try {
            CategoryResponseDto responseDto = categoryService.createCategory(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 등록 중 오류가 발생했습니다.");
        }
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
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        try {
            CategoryResponseDto responseDto = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(responseDto);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * 카테고리 삭제
     *
     * @param categoryId 카테고리 ID
     * @return 상태 응답
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


}
