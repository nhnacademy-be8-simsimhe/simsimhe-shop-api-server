package com.simsimbookstore.apiserver.books.book.controller;

import com.simsimbookstore.apiserver.books.book.aladin.AladinApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final AladinApiService aladinApiService;

    public BookController(AladinApiService aladinApiService) {
        this.aladinApiService = aladinApiService;
    }

    /**
     * 알라딘 api로 데이터베이스에 삽입
     *  현재 작가이름으로 검색쿼리 구현으로 작가로 검색하면 검색내용이 DB에 들어감
     * @return
     */
    @GetMapping("/api/books/aladin")
    public String fetchBooks() {
        try {
            aladinApiService.fetchAndSaveBestsellerBooks();
            return "Books have been fetched and saved successfully.";
        } catch (Exception e) {
            return "Error occurred: " + e.getMessage();
        }
    }
//    @GetMapping("/api/books/fetch")
//    public String fetchBooks(@RequestParam String query) {
//        try {
//            aladinApiService.fetchAndSaveBestsellerBooks();
//            return "Books have been fetched and saved successfully.";
//        } catch (Exception e) {
//            return "Error occurred: " + e.getMessage();
//        }
//    }
}