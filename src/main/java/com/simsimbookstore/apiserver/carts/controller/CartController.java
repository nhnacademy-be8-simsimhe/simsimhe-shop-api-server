package com.simsimbookstore.apiserver.carts.controller;


import com.simsimbookstore.apiserver.carts.dto.CartRequestDto;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<CartResponseDto> getBookForCart(@PathVariable(name = "bookId") String bookId,
                                                          @RequestParam(name = "quantity") int quantity) {
        CartResponseDto responseDto = cartService.getBookForCart(bookId, quantity);

        return ResponseEntity.ok().body(responseDto);
    }


    /**
     * 레디스에 있는 DB로 옮기기
     * @param userId
     * @param requestDtoList
     * @return
     */
    @PutMapping("/migrate/user/{userId}")
    public ResponseEntity CartToDB(@PathVariable(name = "userId") Long userId,
                                   @RequestBody List<CartRequestDto> requestDtoList) {
        cartService.CartToDb(userId, requestDtoList);

        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
