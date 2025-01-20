package com.simsimbookstore.apiserver.books.tag.controller;


import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.mapper.TagMapper;
import com.simsimbookstore.apiserver.books.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class TagController {

    private final TagService tagService;


    /**
     * 태그 생성
     *
     * @param tagRequestDto 태그를 저장하려는 객체
     * @return
     */
    @PostMapping("/tags")
    public ResponseEntity<TagResponseDto> saveTag(@RequestBody @Valid TagRequestDto tagRequestDto) {

        TagResponseDto responseDto = tagService.createTag(tagRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    /**
     * 모든 태그를 조회합니다 페이징 조회 x
     *
     * @return
     */
    @GetMapping("/tags/list")
    public ResponseEntity<List<TagResponseDto>> findAllTag() {

        List<TagResponseDto> tags = tagService.getAlltag();

        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    /**
     * 태그를 삭제합니다. 연관되어있는 태그들은 삭제가 안됌 외래키 제약조건때문에
     *
     * @param tagId
     * @return
     */
    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "tagId") Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 태그 수정
     *
     * @param tagId
     * @param requestDto
     * @return
     */
    @PutMapping("/tags/{tagId}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable(name = "tagId") Long tagId,
                                                    @RequestBody @Valid TagRequestDto requestDto) {
        TagResponseDto tagResponseDto = tagService.updateTag(tagId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(tagResponseDto);
    }

    /**
     * 태그 상세조회
     *
     * @param tagId
     * @return
     */
    @GetMapping("/tags/{tagId}")
    public ResponseEntity<TagResponseDto> getTag(@PathVariable(name = "tagId") Long tagId) {
        Tag tag = tagService.getTag(tagId);
        TagResponseDto tagResponseDto = TagMapper.toTagResponseDto(tag);
        return ResponseEntity.status(HttpStatus.OK).body(tagResponseDto);
    }

}
