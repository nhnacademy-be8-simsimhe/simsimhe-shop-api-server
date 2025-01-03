package com.simsimbookstore.apiserver.books.book.aladin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookAladinController {

    private final AladinApiService aladinApiService;



    /**
     * 알라딘 api로 데이터베이스에 삽입
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
}