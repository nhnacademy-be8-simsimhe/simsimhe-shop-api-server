package com.simsimbookstore.apiserver.books.tag.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TagResponseDto {

    private Long tagId;

    private String tagName;

}
