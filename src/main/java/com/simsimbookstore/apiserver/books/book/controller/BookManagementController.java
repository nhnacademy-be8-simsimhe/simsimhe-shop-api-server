package com.simsimbookstore.apiserver.books.book.controller;

import com.simsimbookstore.apiserver.books.book.dto.BookGiftResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.BookStatusResponseDto;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/shop/books")
@RequiredArgsConstructor
@Slf4j
public class BookManagementController {

    private final BookManagementService bookManagementService;

    /**
     * 도서 등록
     *
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookRequestDto requestDto) {
        BookResponseDto bookResponseDto = bookManagementService.registerBook(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
    }
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<BookResponseDto> createBook(
//            @RequestPart("requestDto") @Valid BookRequestDto requestDto,
//            @RequestPart("thumbnail") MultipartFile thumbnail
//    ) {
//        System.out.println("📌 Received BookRequestDto: " + requestDto);
//        System.out.println("📌 Received File: " + thumbnail.getOriginalFilename());
//        BookResponseDto bookResponseDto = bookManagementService.registerBook(requestDto, thumbnail);
//        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
//    }


    /**
     * 도서 수정인데 도서 상태는 수정 안되게 되어있음
     *
     * @param bookId
     * @param requestDto
     * @return
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable(name = "bookId") Long bookId, @RequestBody @Valid BookRequestDto requestDto) {
        BookResponseDto bookResponseDto = bookManagementService.updateBook(bookId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponseDto);
    }

    /**
     * 도서 상태 수정 도서 상태만 수정가능 다른 컬럼들은 수정 x
     *
     * @param bookId
     * @param bookRequestDto
     * @return
     */
    @PutMapping("/status/{bookId}")
    public ResponseEntity<BookStatusResponseDto> modifyBookStatus(@PathVariable(name = "bookId") Long bookId, @RequestBody @Valid BookRequestDto bookRequestDto) {
        BookStatusResponseDto bookStatusResponseDto = bookManagementService.modifyBookStatus(bookId, bookRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(bookStatusResponseDto);
    }

    /**
     * 수량 추가,감소 로직
     *
     * @param bookId
     * @param quantity
     * @return
     */
    @PutMapping("/quantity/{bookId}")
    public ResponseEntity<Integer> modifyBookQuantity(@PathVariable(name = "bookId") Long bookId, @RequestParam(name = "quantity") int quantity) {
        int updateQuantity = bookManagementService.modifyQuantity(bookId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(updateQuantity);
    }

    /**
     * 상품포장 변경
     *
     * @param bookId
     * @param bookRequestDto
     * @return
     */
    @PutMapping("/gift/{bookId}")
    public ResponseEntity<BookGiftResponse> modifyGift(@PathVariable(name = "bookId") Long bookId, @RequestBody @Valid BookRequestDto bookRequestDto) {
        BookGiftResponse bookGiftResponse = bookManagementService.modifyBookGift(bookId, bookRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(bookGiftResponse);

    }

}
