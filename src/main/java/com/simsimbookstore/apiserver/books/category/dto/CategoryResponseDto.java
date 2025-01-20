package com.simsimbookstore.apiserver.books.category.dto;


import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CategoryResponseDto {

    private Long categoryId;
    private String categoryName;
    private Long parentId; // 부모 카테고리 ID
    private String parentName; // 부모 카테고리 이름
    private List<CategoryResponseDto> children; // 자식 카테고리 목록
}
