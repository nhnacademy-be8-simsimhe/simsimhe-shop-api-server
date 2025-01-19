package com.simsimbookstore.apiserver.elastic.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookElasticsearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class CustomRepositoryImplTest {

    @Mock
    private ElasticsearchClient client;

    @InjectMocks
    private CustomRepositoryImpl customRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("다중 검색 성공 테스트 - 가격 낮은 순 정렬")
    void findByMultipleFields_ShouldUseAscendingOrder_WhenSortIsPriceLow() throws IOException {
        // given
        String word = "test";
        String sort = "price_low"; // Ascending order를 테스트
        int page = 1;
        int size = 12;

        SearchBook book = new SearchBook();
        book.setId(1L);
        book.setTitle("Test Book");

        Hit<SearchBook> hit = mock(Hit.class);
        when(hit.source()).thenReturn(book);


        var totalHits = mock(co.elastic.clients.elasticsearch.core.search.TotalHits.class);
        when(totalHits.value()).thenReturn(1L);

        var hitsMetadata = mock(co.elastic.clients.elasticsearch.core.search.HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit));
        when(hitsMetadata.total()).thenReturn(totalHits);

        SearchResponse<SearchBook> mockResponse = mock(SearchResponse.class);
        when(mockResponse.hits()).thenReturn(hitsMetadata);


        ArgumentCaptor<Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>>> captor = ArgumentCaptor.forClass(Function.class);
        when(client.search(captor.capture(), eq(SearchBook.class))).thenReturn(mockResponse);


        Page<SearchBook> result = customRepository.findByMultipleFields(word, sort, page);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());

        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> capturedFunction = captor.getValue();
        SearchRequest.Builder builder = new SearchRequest.Builder();
        capturedFunction.apply(builder);
        SearchRequest request = builder.build();

        assertEquals(SortOrder.Asc, request.sort().get(0).field().order()); // Ascending order 확인
        verify(client, times(1)).search(any(Function.class), eq(SearchBook.class));
    }


    @Test
    @DisplayName("다중 검색 성공 테스트 - 가격 높은 순 정렬")
    void findByMultipleFields_ShouldUseDescendingOrder_WhenSortIsNotPriceLow() throws IOException {

        String word = "test";
        String sort = "popular";
        int page = 1;
        int size = 12;

        SearchBook book = new SearchBook();
        book.setId(1L);
        book.setTitle("Test Book");

        Hit<SearchBook> hit = mock(Hit.class);
        when(hit.source()).thenReturn(book);

        var totalHits = mock(co.elastic.clients.elasticsearch.core.search.TotalHits.class);
        when(totalHits.value()).thenReturn(1L);

        var hitsMetadata = mock(co.elastic.clients.elasticsearch.core.search.HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit));
        when(hitsMetadata.total()).thenReturn(totalHits);

        SearchResponse<SearchBook> mockResponse = mock(SearchResponse.class);
        when(mockResponse.hits()).thenReturn(hitsMetadata);


        ArgumentCaptor<Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>>> captor = ArgumentCaptor.forClass(Function.class);
        when(client.search(captor.capture(), eq(SearchBook.class))).thenReturn(mockResponse);


        Page<SearchBook> result = customRepository.findByMultipleFields(word, sort, page);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());


        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> capturedFunction = captor.getValue();
        SearchRequest.Builder builder = new SearchRequest.Builder();
        capturedFunction.apply(builder);
        SearchRequest request = builder.build();

        assertEquals(SortOrder.Desc, request.sort().get(0).field().order()); // Descending order 확인
        verify(client, times(1)).search(any(Function.class), eq(SearchBook.class));
    }


    @Test
    @DisplayName("다중 검색 실패 테스트 - 예외 발생 시 처리")
    void findByMultipleFields_ShouldThrowException_WhenElasticsearchFails() throws IOException {

        String word = "test";
        String sort = "popular";
        int page = 1;

        when(client.search(any(Function.class), eq(SearchBook.class))).thenThrow(new IOException("Elasticsearch error"));


        Exception exception = assertThrows(SearchBookElasticsearchException.class, () ->
                customRepository.findByMultipleFields(word, sort, page));


        assertTrue(exception.getMessage().contains("엘라스틱서치 쿼리를 실행하는 중 실패했습니다."));
        verify(client, times(1)).search(any(Function.class), eq(SearchBook.class));
    }


    @Test
    @DisplayName("문서 존재 여부 확인 성공 테스트 - 정확한 INDEX와 ID 확인")
    void isExist_ShouldVerifyIndexAndId_WhenDocumentExists() throws IOException {

        String id = "1";

        BooleanResponse booleanResponse = mock(BooleanResponse.class);
        when(booleanResponse.value()).thenReturn(true);


        doAnswer(invocation -> {
            Function<ExistsRequest.Builder, ObjectBuilder<ExistsRequest>> function = invocation.getArgument(0);
            ExistsRequest.Builder builder = new ExistsRequest.Builder();
            function.apply(builder);
            ExistsRequest request = builder.build();

            assertEquals("simsimhe-books", request.index());
            assertEquals("1", request.id());

            return booleanResponse;
        }).when(client).exists(any(Function.class));


        boolean result = customRepository.isExist(id);


        assertTrue(result);
        verify(client, times(1)).exists(any(Function.class));
    }


    @Test
    @DisplayName("문서 존재 여부 확인 실패 테스트 - 정확한 INDEX와 ID 확인")
    void isExist_ShouldVerifyIndexAndId_WhenDocumentDoesNotExist() throws IOException {

        String id = "1";

        BooleanResponse booleanResponse = mock(BooleanResponse.class);
        when(booleanResponse.value()).thenReturn(false);


        doAnswer(invocation -> {
            Function<ExistsRequest.Builder, ObjectBuilder<ExistsRequest>> function = invocation.getArgument(0);
            ExistsRequest.Builder builder = new ExistsRequest.Builder();
            function.apply(builder);
            ExistsRequest request = builder.build();

            assertEquals("simsimhe-books", request.index());
            assertEquals("1", request.id());

            return booleanResponse;
        }).when(client).exists(any(Function.class));


        boolean result = customRepository.isExist(id);


        assertFalse(result);
        verify(client, times(1)).exists(any(Function.class));
    }

    @Test
    @DisplayName("정렬 키워드 변환 테스트 - 올바른 키워드")
    void convertSortWord_ShouldReturnCorrectField_WhenValidSortProvided() {

        assertEquals("bookSellCount", customRepository.convertSortWord("popular"), "popular 키워드가 변환되지 않았습니다.");
        assertEquals("publishedAt", customRepository.convertSortWord("latest"), "latest 키워드가 변환되지 않았습니다.");
        assertEquals("salePrice", customRepository.convertSortWord("price_high"), "price_high 키워드가 변환되지 않았습니다.");
        assertEquals("salePrice", customRepository.convertSortWord("price_low"), "price_low 키워드가 변환되지 않았습니다.");
        assertEquals("reviewCount", customRepository.convertSortWord("review"), "review 키워드가 변환되지 않았습니다.");
    }


    @Test
    @DisplayName("정렬 키워드 변환 테스트 - 잘못된 키워드")
    void convertSortWord_ShouldReturnEmptyString_WhenInvalidSort() {
        String invalidSort = "invalid";

        String result = customRepository.convertSortWord(invalidSort);

        assertEquals("", result);
    }

}