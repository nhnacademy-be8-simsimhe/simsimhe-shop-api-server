package com.simsimbookstore.apiserver.elastic.controller;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.elastic.service.ElasticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/elastic")
@RestController
public class ElasticController {
    public final ElasticService elasticService;

    public ElasticController(ElasticService elasticService) {
        this.elasticService = elasticService;
    }

    @PostMapping("/index/{indexName}")
    public ResponseEntity createIndex(@PathVariable String indexName){
        elasticService.createIndex(indexName);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/document")
    public ResponseEntity saveData(@RequestBody Map<String, Object> payload){

//        String id = payload.get("id").toString();
//        String title = payload.get("title").toString();
//        List<String> tags = (List<String>) payload.get("tags");
//        String author = payload.get("author").toString();
//
//        Book book = new Book(id, title, author, tags);
//        elasticService.saveBook("books", book);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/document/{id}")
    public Book updateData(@PathVariable String id, @RequestBody Map<String, Object> updateFields){

        return elasticService.updateBook("books", id, updateFields);
    }




    @GetMapping("/document/{word}")
    public List<Book> getDatas(@PathVariable String word){

        return elasticService.searchByWord("books",word);
    }


    @DeleteMapping("/document")
    public ResponseEntity deleteData(){

        elasticService.deleteAll("books");

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/document/{id}")
    public ResponseEntity deleteData(@PathVariable String id){

        elasticService.deleteBook(id);

        return ResponseEntity.ok().build();
    }
}
