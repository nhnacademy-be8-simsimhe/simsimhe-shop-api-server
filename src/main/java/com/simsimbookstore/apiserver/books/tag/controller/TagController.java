package com.simsimbookstore.apiserver.books.tag.controller;


import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 태그 생성
     * @param tagRequestDto 태그를 저장하려는 객체
     * @param bindingResult 에러처리
     * @return
     */
    @PostMapping("/tags")
    public ResponseEntity<?> saveTag(@RequestBody @Valid TagRequestDto tagRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        TagResponseDto responseDto = tagService.saveTag(tagRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    /**
     * 모든 태그를 조회합니다 페이징 조회 x
     * @return
     */
    @GetMapping("/tags/list")
    public ResponseEntity<List<TagResponseDto>> findAllTag(){

        List<TagResponseDto> tags = tagService.getAlltag();

        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    /**
     * 페이지별로 태그 조회하기
     * <a href="http://localhost:8020/api/admin/tags?page=0&size=2">...</a>
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/tags")
    public Page<TagResponseDto> getAllTags(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tagService.getAllTags(pageable);
    }

    /**
     * 태그를 삭제합니다. 연관되어있는 태그들은 삭제가 안됌 외래키 제약조건때문에
     * @param tagId
     * @return
     */
    @DeleteMapping("/tags/{tagId}")
    private ResponseEntity<?> deleteTag(@PathVariable(name = "tagId") Long tagId){

        tagService.deleteTag(tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
