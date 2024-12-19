package com.simsimbookstore.apiserver.books.tag.service;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    TagResponseDto saveTag(TagRequestDto tagRequestDto);

    List<TagResponseDto> getAlltag();

    Tag findById(Long tagId);

    Page<TagResponseDto> getAllTags(Pageable pageable);

    void deleteTag(Long tagId);
}
