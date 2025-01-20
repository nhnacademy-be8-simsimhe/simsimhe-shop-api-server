package com.simsimbookstore.apiserver.carts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.controller.BookGetController;
import com.simsimbookstore.apiserver.books.book.service.BookGetService;
import com.simsimbookstore.apiserver.carts.dto.CartRequestDto;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.entity.Cart;
import com.simsimbookstore.apiserver.carts.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CartService cartService() {
            return Mockito.mock(CartService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(cartService);
    }

    @Test
    @DisplayName("장바구니에 담을 도서 조회 API 테스트")
    void getBookForCart_Success() throws Exception {
        // Given

        CartResponseDto cartResponseDto = CartResponseDto.builder().bookId(1L).bookQuantity(100).quantity(100).userId("1").build();

        when(cartService.getBookForCart(anyString(), anyInt())).thenReturn(cartResponseDto);

        // When & Then
        mockMvc.perform(get("/api/shop/cart/book/1")
                        .param("quantity", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1L))
                .andExpect(jsonPath("$.bookQuantity").value(100))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(cartService, times(1)).getBookForCart(anyString(), anyInt());
    }

    @Test
    @DisplayName("레디스 장바구니 데이터를 DB로 저장 API 테스트")
    void cartToDb_Success() throws Exception {
        // Given
        Long userId = 1L;
        List<CartRequestDto> cartRequestDtos = new ArrayList<>();
        CartRequestDto cartResponseDto1 = CartRequestDto.builder().bookId("1").quantity(100).userId(String.valueOf(userId)).build();
        CartRequestDto cartResponseDto2 = CartRequestDto.builder().bookId("2").quantity(100).userId(String.valueOf(userId)).build();
        cartRequestDtos.add(cartResponseDto1);
        cartRequestDtos.add(cartResponseDto2);

        doNothing().when(cartService).cartToDb(anyLong(), anyList());

        // When & Then
        mockMvc.perform(put("/api/shop/cart/migrate/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequestDtos)))
                .andExpect(status().isOk());

        verify(cartService, times(1)).cartToDb(anyLong(), anyList());
    }


}