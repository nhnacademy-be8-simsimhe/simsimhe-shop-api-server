package com.simsimbookstore.apiserver.books.book.service;


import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.mapper.BookMapper;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }



}
