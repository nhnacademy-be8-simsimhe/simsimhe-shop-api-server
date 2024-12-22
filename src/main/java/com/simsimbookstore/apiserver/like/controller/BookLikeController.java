package com.simsimbookstore.apiserver.like.controller;


import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;
import com.simsimbookstore.apiserver.like.service.BookLikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/likes")
public class BookLikeController {

    private final BookLikeService bookLikeService;

    public BookLikeController(BookLikeService bookLikeService) {
        this.bookLikeService = bookLikeService;
    }

    /**
     * 좋아요를 설정,취소하는 컨트롤러
     *
     * @param requestDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public ResponseEntity<?> setBookLike(@RequestBody @Valid BookLikeRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        try {
            BookLikeResponseDto responseDto = bookLikeService.setBookLike(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좋아요 등록이 안됩니다");
        }
    }


    /**
     * 회원이 좋아요를 누른 총 개수를 반환
     *
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
