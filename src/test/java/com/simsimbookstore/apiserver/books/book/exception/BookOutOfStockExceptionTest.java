package com.simsimbookstore.apiserver.books.book.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookOutOfStockExceptionTest {
    @Test
    void testExceptionMessage() {
        // 예외 메시지를 설정하여 예외 생성
        String expectedMessage = "책이 품절되었습니다.";
        BookOutOfStockException exception = new BookOutOfStockException(expectedMessage);

        // 예외 메시지가 올바르게 저장되는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionThrown() {
        // 예외 발생 테스트
        Exception exception = assertThrows(BookOutOfStockException.class, () -> {
            throw new BookOutOfStockException("재고 부족");
        });

        // 예외 메시지 검증
        assertEquals("재고 부족", exception.getMessage());
    }


}