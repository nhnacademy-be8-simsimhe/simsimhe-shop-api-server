package com.simsimbookstore.apiserver.books.tag.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TagResponseDto {

    private Long tagId;

    private String tagName;

}
