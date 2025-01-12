package com.simsimbookstore.apiserver.elastic.controller;


import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/elastic")
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


//        TestBook book = new TestBook(0L, id, title, author, tags);
//        testElasticService.saveBook("books", book);

        SearchBook book = new SearchBook(id, title, description,author, tags, date,salePrice,bookSellCount,reviewCount);
        elasticService.createBook(book);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/save")
    public ResponseEntity<?> saveAll(){
        elasticService.saveAll("simsim_test.json");
        return ResponseEntity.ok("all data is saved");
    }

//
//    @PostMapping("/document/{id}")
//    public SearchBook updateData(@PathVariable String id, @RequestBody Map<String, Object> updateFields){
//
//        return testElasticService.updateBook("books", id, updateFields);
//    }




    @GetMapping("/document")
    public List<SearchBook> getDatas(@RequestParam String word){
        return elasticService.searchBookByWord(word);
//        return testElasticService.searchByWord("simsimhe-books",word);
    }


//    @DeleteMapping("/document")
//    public ResponseEntity deleteData(){
//
//        testElasticService.deleteAll("books");
//
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("/document/{id}")
    public ResponseEntity deleteData(@PathVariable String id){

        elasticService.deleteBook(id);

        return ResponseEntity.ok().build();
    }
}
