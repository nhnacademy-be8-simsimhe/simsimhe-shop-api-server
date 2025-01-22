package com.simsimbookstore.apiserver.books.tag.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TagResponseDto {

    private Long tagId;

    private String tagName;

}
