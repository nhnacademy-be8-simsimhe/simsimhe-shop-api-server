package com.simsimbookstore.apiserver.elastic.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookElasticsearchException;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.List;


@Slf4j
@Repository
public class CustomRepositoryImpl implements CustomRepository{

    public static final String INDEX = "simsimhe-books";
    ElasticsearchClient client;

    @Autowired
    public CustomRepositoryImpl(ElasticsearchClient client, LogbackMetrics logbackMetrics) {
        this.client = client;
    }


    @Override
    public void save(SearchBook book) throws IOException {

        client.index(i -> i.index(INDEX).id(String.valueOf(book.getId())).document(book));
    }

    @Override
    public void delete(String id) throws IOException {
        client.delete(d -> d.index(INDEX).id(String.valueOf(id)));
    }


    @Override
    public Page<SearchBook> findByMultipleFields(String word, String sort, int page) {  //다중 검색
        String sortAt = convertSortWord(sort);

        try {
            SearchResponse<SearchBook> response = client.search(s -> s
                            .index(INDEX)
                            .from((page -1) * 12)
                            .size(12)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(word)
                                            .fields(List.of("title^5", "author^5", "description^1", "tags^2"))  // 가중치 지정 , 타이틀과 작가가 검색시 가장 높은 순위로 검색이 됨
                                            .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.Or)
                                    )
                            )
                            .sort(so -> so
                                    .field(f -> f
                                            .field(sortAt) // 정렬 기준 필드
                                            .order(sort.equals("price_low") ? SortOrder.Asc : SortOrder.Desc) // 가격 낮은 순은 오름차순 정렬, 나머지는 내림차순 정렬
                                    )
                            )
                    ,
                    SearchBook.class);

            log.info("total data : {}", response.hits().total().value());

            List<SearchBook> results = response.hits().hits().stream().map(Hit::source).toList();

            long totalCount = response.hits().total().value();

            Pageable pageable = PageRequest.of((page -1), 12);


            return new PageImpl<>(results, pageable, totalCount);
        } catch (IOException e) {
            throw new SearchBookElasticsearchException("엘라스틱서치 쿼리를 실행하는 중 실패했습니다. 검색어: "+word, e);
        }
    }

    @Override
    public boolean isExist(String id) throws IOException {
        return client.exists(e -> e
                .index(INDEX) // 인덱스 이름
                .id(id)       // 확인할 문서 ID
        ).value();
    }

    public String convertSortWord(String sort){
        return switch (sort) {
            case "popular" -> "bookSellCount";
            case "latest" -> "publishedAt";
            case "price_high", "price_low" -> "salePrice";
            case "review" -> "reviewCount";
            default -> "";
        };

    }
}
