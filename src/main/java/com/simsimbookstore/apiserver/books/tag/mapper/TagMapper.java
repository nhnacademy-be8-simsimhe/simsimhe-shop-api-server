package com.simsimbookstore.apiserver.books.tag.mapper;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;

public class TagMapper {

    private TagMapper(){
        throw new UnsupportedOperationException("TagMapper는 인스턴스화 할수없음");
    }

    public static TagResponseDto toTagResponseDto(Tag tag){
        return TagResponseDto.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .build();
    }

    public static Tag toTag(TagRequestDto tagRequestDto){
        return Tag.builder()
                .tagName(tagRequestDto.getTagName())
                .build();
    }
}
