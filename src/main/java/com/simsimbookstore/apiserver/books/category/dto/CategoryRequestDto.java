package com.simsimbookstore.apiserver.books.category.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "등록할 카테고리 이름을 공백없이 적어주세요")
    private String categoryName;

    private Long parentId; // 부모 카테고리 ID


}
