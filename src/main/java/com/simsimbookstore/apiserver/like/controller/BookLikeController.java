package com.simsimbookstore.apiserver.like.controller;


import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;
import com.simsimbookstore.apiserver.like.service.BookLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/likes")
public class BookLikeController {

    private final BookLikeService bookLikeService;


    /**
     * 좋아요를 설정,취소하는 컨트롤러
     *
     * @param requestDto
     * @return
     */
    @PutMapping
    public ResponseEntity<BookLikeResponseDto> setBookLike(@RequestBody @Valid BookLikeRequestDto requestDto) {

        BookLikeResponseDto responseDto = bookLikeService.setBookLike(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }


    /**
     * 회원이 좋아요를 누른 총 개수를 반환
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Long> getUserLikesNum(@PathVariable("userId") Long userId) {
        Long userLikeNum = bookLikeService.getUserLikeNum(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userLikeNum);
    }

}
