package com.simsimbookstore.apiserver.orders.order.service.Impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderListServiceImpl implements OrderListService {

    private final BookRepository bookRepository;

    @Override
    public List<BookListResponseDto> toBookOrderList(List<BookListRequestDto> dtos) {
        List<BookListResponseDto> bookListResponseDtos = new ArrayList<>();
        for (BookListRequestDto dto : dtos) {
            Book book = bookRepository.findById(dto.getBookId()).orElseThrow();
            BookListResponseDto repDto = BookListResponseDto.builder()
                    .bookId(dto.getBookId())
                    .price(book.getSaleprice())
                    .quantity(dto.getQuantity())
                    .title(book.getTitle())
                    .build();

            bookListResponseDtos.add(repDto);
        }

        return bookListResponseDtos;
    }

}
