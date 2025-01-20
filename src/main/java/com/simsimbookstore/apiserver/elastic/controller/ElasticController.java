package com.simsimbookstore.apiserver.elastic.controller;


import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.elastic.dto.SearchBookDto;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/shop/elastic")
@RestController
@RequiredArgsConstructor
public class ElasticController {
    public final ElasticService elasticService;


    @PostMapping("/document")
    public ResponseEntity<Void> saveData(@RequestBody SearchBookDto searchBookDto) {

        SearchBook searchBook = new SearchBook(
                searchBookDto.getId(),
                searchBookDto.getTitle(),
                searchBookDto.getDescription(),
                searchBookDto.getAuthor(),
                searchBookDto.getBookImage(),
                searchBookDto.getTags(),
                searchBookDto.getPublishedAt(),
                searchBookDto.getSalePrice(),
                0,
                0
        );

        elasticService.createBook(searchBook);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/save")
    public ResponseEntity<String> saveAll() {
        elasticService.saveAll("simsim.json");
        return ResponseEntity.ok("all data is saved");
    }


    @GetMapping("/document")
    public PageResponse<SearchBook> getDatas(@RequestParam String keyword, @RequestParam(defaultValue = "popular") String sort, @RequestParam(defaultValue = "1") int page) {

        return elasticService.searchBookByWord(keyword, sort, page);

    }


    @DeleteMapping("/document/{id}")
    public ResponseEntity<String> deleteData(@PathVariable String id) {

        elasticService.deleteBook(id);

        return ResponseEntity.ok().build();
    }
}
