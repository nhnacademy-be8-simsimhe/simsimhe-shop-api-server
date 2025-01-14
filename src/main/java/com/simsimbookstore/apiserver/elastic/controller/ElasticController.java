package com.simsimbookstore.apiserver.elastic.controller;


import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/shop/elastic")
@RestController
@RequiredArgsConstructor
public class ElasticController {
    public final ElasticService elasticService;



    @PostMapping("/document")
    public ResponseEntity saveData(@RequestBody Map<String, Object> payload){

        Long id = Long.parseLong(payload.get("id").toString());
        String title = payload.get("title").toString();
        String author = payload.get("author").toString();
        String description = payload.get("description").toString();
        String date = payload.get("date").toString();
        long salePrice = Long.parseLong(payload.get("salePrice").toString());
        long bookSellCount = Long.parseLong(payload.get("bookSellCount").toString());
        long reviewCount = Long.parseLong(payload.get("reviewCount").toString());
        List<String> tags = (List<String>) payload.get("tags");


        SearchBook book = new SearchBook(id, title, description,author, "",tags, date,salePrice,bookSellCount,reviewCount);
        elasticService.createBook(book);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/save")
    public ResponseEntity<?> saveAll(){
        elasticService.saveAll("simsim.json");
        return ResponseEntity.ok("all data is saved");
    }



    @GetMapping("/document")
    public PageResponse<SearchBook> getDatas(@RequestParam String keyword, @RequestParam(defaultValue = "popular") String sort, @RequestParam(defaultValue = "0") int page){

        return elasticService.searchBookByWord(keyword, sort, page);

    }



    @DeleteMapping("/document/{id}")
    public ResponseEntity deleteData(@PathVariable String id){

        elasticService.deleteBook(id);

        return ResponseEntity.ok().build();
    }
}
