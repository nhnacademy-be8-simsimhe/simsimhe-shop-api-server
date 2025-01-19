package com.simsimbookstore.apiserver.elastic.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.exception.FileParsingException;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookExistenceCheckFailedException;
import com.simsimbookstore.apiserver.elastic.repository.SearchBookRepository;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElasticServiceTest {

    @InjectMocks
    private ElasticService elasticService;

    @Mock
    private SearchBookRepository repository;

    @Mock
    private ResourceLoader resourceLoader;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("도서 생성 - 존재하지 않을 경우 저장 성공")
    void createBook_ShouldSaveBook_WhenNotExists() throws IOException {
        SearchBook book = new SearchBook();
        book.setId(1L);

        when(repository.isExist("1")).thenReturn(false);

        elasticService.createBook(book);

        verify(repository, times(1)).save(book);
    }

    @Test
    @DisplayName("도서 생성 - 이미 존재할 경우 예외 발생")
    void createBook_ShouldThrowException_WhenBookAlreadyExists() throws IOException {
        SearchBook book = new SearchBook();
        book.setId(1L);
        when(repository.isExist("1")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> elasticService.createBook(book));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("도서 생성 - 저장 실패 시 예외 발생")
    void createBook_ShouldThrowException_WhenSaveFails() throws IOException {
        SearchBook book = new SearchBook();
        book.setId(1L);
        when(repository.isExist("1")).thenReturn(false);
        doThrow(new RuntimeException("Save failed")).when(repository).save(book);

        assertThrows(RuntimeException.class, () -> elasticService.createBook(book));
        verify(repository, times(1)).save(book);
    }


    @Test
    @DisplayName("도서 삭제 - 존재할 경우 삭제 성공")
    void deleteBook_ShouldDeleteBook_WhenExists() throws IOException {
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(true);


        elasticService.deleteBook(bookId);


        verify(repository, times(1)).deleteById(Long.parseLong(bookId));
    }

    @Test
    @DisplayName("도서 삭제 - 존재하지 않을 경우 예외 발생")
    void deleteBook_ShouldThrowException_WhenBookDoesNotExist() throws IOException {
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> elasticService.deleteBook(bookId));
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("도서 존재 여부 확인 - 존재할 경우 true 반환")
    void isExist_ShouldReturnTrue_WhenBookExists() throws IOException {
        String bookId = "1";

        when(repository.isExist(bookId)).thenReturn(true);


        boolean exists = elasticService.isExist(bookId);

        assertTrue(exists);
    }

    @Test
    @DisplayName("도서 존재 여부 확인 - 존재하지 않을 경우 false 반환")
    void isExist_ShouldReturnFalse_WhenBookDoesNotExist() throws IOException {
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(false);

        boolean exists = elasticService.isExist(bookId);

        assertFalse(exists);
    }

    @Test
    @DisplayName("도서 존재 여부 확인 - Elasticsearch 호출 실패 시 예외 발생")
    void isExist_ShouldThrowException_WhenElasticsearchFails() throws IOException {
        String bookId = "1";

        when(repository.isExist(bookId)).thenThrow(new IOException("Elasticsearch error"));

        Exception exception = assertThrows(SearchBookExistenceCheckFailedException.class, () ->
                elasticService.isExist(bookId));

        assertTrue(exception.getMessage().contains("도서 존재 여부 확인에 실패했습니다"));
    }


    @Test
    @DisplayName("키워드 검색 - 검색 결과 반환")
    void searchBookByWord_ShouldReturnBooks_WhenCalled() {
        String keyword = "test";
        String sort = "asc";
        int page = 1;
        int size = 12;

        Pageable pageable = PageRequest.of(page - 1, size);
        List<SearchBook> bookList = List.of(
                new SearchBook(1L, "Test Book 1", "Description 1", "Author 1", null, null, null, 1000, 10, 5),
                new SearchBook(2L, "Test Book 2", "Description 2", "Author 2", null, null, null, 2000, 15, 7)
        );
        Page<SearchBook> books = new PageImpl<>(bookList, pageable, bookList.size());

        when(repository.findByMultipleFields(keyword, sort, page)).thenReturn(books);

        PageResponse<SearchBook> result = elasticService.searchBookByWord(keyword, sort, page);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getData().size());
        assertEquals("Test Book 1", result.getData().get(0).getTitle());
        assertEquals("Test Book 2", result.getData().get(1).getTitle());
        assertEquals(page, result.getCurrentPage());
        assertEquals(1, result.getStartPage());
        assertEquals(1, result.getEndPage());
        verify(repository, times(1)).findByMultipleFields(keyword, sort, page);
    }


    @Test
    @DisplayName("전체 저장 실패 - JSON 파일 파싱 오류")
    void saveAll_ShouldThrowException_WhenJsonFileParsingFails() throws IOException {
        String filePath = "invalid_file.json";
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath))
                .thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("Resource not found"));

        Exception exception = assertThrows(FileParsingException.class, () ->
                elasticService.saveAll(filePath));

        assertTrue(exception.getMessage().contains("Json 파일을 파싱하는 중 오류가 발생했습니다"));
        verify(resourceLoader, times(1)).getResource("classpath:" + filePath);
    }


    @Test
    @DisplayName("전체 저장 - 파일 내 도서 모두 저장 성공")
    void saveAll_ShouldSaveAllBooks_WhenCalled() throws IOException {
        String filePath = "simsim_test.json"; // 파일 경로
        String jsonContent = "[\n" +
                "  {\"id\": 1, \"title\": \"Test Book 1\", \"description\": \"Description 1\", \"author\": \"Author 1\"},\n" +
                "  {\"id\": 2, \"title\": \"Test Book 2\", \"description\": \"Description 2\", \"author\": \"Author 2\"}\n" +
                "]";

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(jsonContent.getBytes()));

        elasticService = new ElasticService(repository, resourceLoader);

        elasticService.saveAll(filePath);

        verify(repository, times(2)).save(any(SearchBook.class));
        verify(resourceLoader, times(1)).getResource("classpath:" + filePath);
    }


    @Test
    @DisplayName("JSON 파일 파싱 성공 테스트")
    void parseJsonFile_ShouldReturnBookList_WhenFileIsValid() throws IOException {
        String filePath = "simsim_test.json";
        String jsonContent = "[\n" +
                "  {\"id\": 1, \"title\": \"Test Book 1\"},\n" +
                "  {\"id\": 2, \"title\": \"Test Book 2\"}\n" +
                "]";

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(jsonContent.getBytes()));

        List<SearchBook> books = elasticService.parseJsonFile(filePath);

        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Test Book 1", books.get(0).getTitle());
        assertEquals("Test Book 2", books.get(1).getTitle());
        verify(resource, times(1)).getInputStream();
    }

    @Test
    @DisplayName("JSON 파일 파싱 실패 테스트 - InputStream 열기 실패")
    void parseJsonFile_ShouldThrowIOException_WhenInputStreamFails() throws IOException {
        String filePath = "invalid.json";

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("Failed to open InputStream"));

        Exception exception = assertThrows(IOException.class, () -> elasticService.parseJsonFile(filePath));

        assertNotNull(exception);
        assertEquals("Failed to open InputStream", exception.getMessage());
        verify(resource, times(1)).getInputStream();
    }


    @Test
    @DisplayName("페이지 응답 생성 - 전체 페이지가 최대 버튼 수보다 작은 경우")
    void getPageResponse_ShouldHandleSmallTotalPages() {
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<SearchBook> books = List.of(new SearchBook(), new SearchBook());
        Page<SearchBook> bookPage = new PageImpl<>(books, pageable, 2); // 총 페이지 수가 1

        PageResponse<SearchBook> response = elasticService.getPageResponse(page, bookPage);

        assertEquals(1, response.getCurrentPage());
        assertEquals(1, response.getStartPage());
        assertEquals(1, response.getEndPage());
        assertEquals(2, response.getTotalElements());
    }

    @Test
    @DisplayName("페이지 응답 생성 - 전체 페이지가 최대 버튼 수보다 큰 경우")
    void getPageResponse_ShouldHandleLargeTotalPages() {

        int page = 5; // 중간 페이지
        int maxPageButtons = 10; // 최대 버튼 수
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<SearchBook> books = Collections.nCopies(100, new SearchBook()); // 많은 데이터로 총 페이지 수 증가
        Page<SearchBook> bookPage = new PageImpl<>(books, pageable, 100); // 총 페이지 수가 10

        PageResponse<SearchBook> response = elasticService.getPageResponse(page, bookPage);

        assertEquals(5, response.getCurrentPage());
        assertEquals(1, response.getStartPage()); // 시작 페이지는 항상 1
        assertEquals(10, response.getEndPage()); // 버튼 수는 최대값으로 제한
        assertEquals(100, response.getTotalElements());
    }


}
