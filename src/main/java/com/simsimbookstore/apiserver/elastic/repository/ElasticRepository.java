package com.simsimbookstore.apiserver.elastic.repository;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Repository
public class ElasticRepository {
    private final ElasticsearchClient client;

    public ElasticRepository(ElasticsearchClient client) {
        this.client = client;
    }

    // 인덱스 생성
    public void createIndex(String index, Map<String, Object> settings) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper
        String jsonSettings = objectMapper.writeValueAsString(settings); // JSON 직렬화

        client.indices().create(c -> c
                .index(index)
                .withJson(new ByteArrayInputStream(jsonSettings.getBytes(StandardCharsets.UTF_8)))
                .mappings(m -> m
                        .properties("title", p -> p.text(t -> t.analyzer("nori_tokenizer")))
                        .properties("price", p -> p.double_(t->t))
                        .properties("authors", p -> p.text(t -> t.analyzer("nori_tokenizer")))
                        .properties("review_count", p -> p.integer(t -> t))
                        .properties("average_score", p -> p.float_(t -> t))
                        .properties("thumbnail_image", p -> p.text(t -> t))
                )
        );


    }

    // 데이터 저장
    public void saveDocument(String index, Book book) throws IOException {
        client.index(i -> i.index(index).document(book));
    }

    // 데이터 조회
    public Book findDocumentById(String index, String id) throws IOException {
        return client.get(g -> g.index(index).id(id), Book.class).source();
    }


    // 다중 검색
    // 제목, 태그 검색 가능

    public List<Book> findDocumentByWord(String index, String word) throws IOException{
        SearchResponse<Book> response = null;
        try {
            response = client.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(word)
                                            .fields("title^2", "tags")  // ^2 -> 가중치를 줌 , title이 우선적으로
                                            .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.Or))) // or 연산자
                    , Book.class);

            for (Hit<Book> hit : response.hits().hits()) {
                System.out.println("ID: " + hit.id());
            }

            return response.hits().hits().stream().map(Hit::source).toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // 데이터 업데이트
    public UpdateResponse<Book> updateDocument(String index, String id, Map<String, Object> updatedFields) throws IOException {
        return client.update(u -> u.index(index).id(id).doc(updatedFields).source(s-> s.fetch(true)), Book.class);
    }

    // 데이터 삭제
    public void deleteDocument(String index, String id) throws IOException {
        client.delete(d -> d.index(index).id(id));
    }


    // 인덱스의 전체 데이터 삭제
    public void deleteALl(String index) throws IOException{
        client.deleteByQuery(d-> d.index(index).query(q-> q.matchAll(m -> m)));
    }
}
