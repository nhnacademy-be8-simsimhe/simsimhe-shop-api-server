package com.simsimbookstore.apiserver.elastic.service;

import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.elastic.repository.ElasticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ElasticService {
    private ElasticRepository repository;


    @Autowired
    public ElasticService(ElasticRepository repository){
        this.repository = repository;

    }


    //CREATE

    //mapping 정보가 없으면 dynamic mapping
    //자동매핑은 사용자의 의도와 다를 수 있고, 검색 성능이 떨어질 수 있음
    public void createIndex(String index){

        Map<String, Object> settings = Map.of(
                "analysis", Map.of(
                        "tokenizer", Map.of(
                                "nori_tokenizer", Map.of(
                                        "type", "nori_tokenizer",
                                        "decompound_mode", "discard"
                                )
                        ),
                        "filter" , Map.of(
                                "nori_pos_filter", Map.of(
                                        "type", "nori_part_of_speech",
                                        "stoptags", List.of("E", "J", "IC")
                                )
                        ),
                        "analyzer", Map.of(
                                "nori_analyzer", Map.of(
                                        "type", "custom",
                                        "tokenizer", "nori_tokenizer",
                                        "filter", List.of("lowercase", "nori_pos_filter")
                                )
                        )
                )
        );

        try {
            repository.createIndex(index, settings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create index", e);
        }
    }


    public void saveBook(String index,  Book book) {
        try {
            repository.saveDocument(index,  book);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save book", e);
        }
    }

    public Book getBookById(String index, String id){
        try {
            return repository.findDocumentById(index, id);
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve book", e);
        }
    }



    // 검색어랑 연관되어 있는 데이터 전체 조회
    public List<Book> searchByWord(String index, String word) {
        try{
            return repository.findDocumentByWord(index, word);
        }catch (IOException e){
            throw new RuntimeException("Failed to search books", e);
        }
    }



    public Book updateBook(String index, String id, Map<String, Object> updateFields){
        try {
            UpdateResponse<Book> response = repository.updateDocument(index, id, updateFields);
            return response.get().source();
        } catch (IOException e) {
            throw new RuntimeException("Failed to update book", e);
        }

    }


    //DELETE

    // 해당 인덱스 전체 삭제
    public void deleteAll(String index){
        try {
            repository.deleteALl(index);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 해당 id 도서 삭제
    public void deleteBook(String id){
        try {
            repository.deleteDocument("test", id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
