package com.simsimbookstore.apiserver.elastic.repository;

import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchBookRepository extends ElasticsearchRepository<SearchBook, Long>, CustomRepository {
}
