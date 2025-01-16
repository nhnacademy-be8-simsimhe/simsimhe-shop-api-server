package com.simsimbookstore.apiserver.elastic.repository;



import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface CustomRepository {

    void save(SearchBook book) throws IOException;
    void delete(String id) throws IOException;
    Page<SearchBook> findByMultipleFields(String word, String field, int page);
    boolean isExist(String id) throws IOException;
}
