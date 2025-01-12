package com.simsimbookstore.apiserver.elastic.service;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.repository.CustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticService {
    private final CustomRepository repository;


    private final ResourceLoader resourceLoader;



    public void createBook(SearchBook book){
        try {
            repository.save(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteBook(String id){
        try {
            repository.delete(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SearchBook> searchBookByWord(String word){
        return repository.findByMultipleFields(word,"salePrice");
    }


    public List<SearchBook> parseJsonFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:"+filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper.readValue(resource.getFile(), new TypeReference<List<SearchBook>>() {});
    }

    public void saveAll(String filePath){

        List<SearchBook> books = null;
        try {
            books = parseJsonFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (SearchBook book : books){
            try {
                repository.save(book);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }



}
