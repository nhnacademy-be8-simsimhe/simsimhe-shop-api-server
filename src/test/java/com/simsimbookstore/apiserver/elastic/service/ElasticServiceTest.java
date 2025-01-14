package com.simsimbookstore.apiserver.elastic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.repository.CustomRepository;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ResourceLoader;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElasticServiceTest {

    @InjectMocks
    private ElasticService elasticService;

    @Mock
    private CustomRepository repository;

    @Mock
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("도서 생성 - 존재하지 않을 경우 저장 성공")
    void createBook_ShouldSaveBook_WhenNotExists() throws IOException {
        // given
        SearchBook book = new SearchBook();
        book.setId(1L);
        when(repository.isExist("1")).thenReturn(false);

        // when
        elasticService.createBook(book);

        // then
        verify(repository, times(1)).save(book);
    }

    @Test
    @DisplayName("도서 생성 - 이미 존재할 경우 예외 발생")
    void createBook_ShouldThrowException_WhenBookAlreadyExists() throws IOException {
        // given
        SearchBook book = new SearchBook();
        book.setId(1L);
        when(repository.isExist("1")).thenReturn(true);

        // when & then
        assertThrows(AlreadyExistException.class, () -> elasticService.createBook(book));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("도서 삭제 - 존재할 경우 삭제 성공")
    void deleteBook_ShouldDeleteBook_WhenExists() throws IOException {
        // given
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(true);

        // when
        elasticService.deleteBook(bookId);

        // then
        verify(repository, times(1)).delete(bookId);
    }

    @Test
    @DisplayName("도서 삭제 - 존재하지 않을 경우 예외 발생")
    void deleteBook_ShouldThrowException_WhenBookDoesNotExist() throws IOException {
        // given
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> elasticService.deleteBook(bookId));
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("도서 존재 여부 확인 - 존재할 경우 true 반환")
    void isExist_ShouldReturnTrue_WhenBookExists() throws IOException {
        // given
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(true);

        // when
        boolean exists = elasticService.isExist(bookId);

        // then
        assertTrue(exists);
    }

    @Test
    @DisplayName("도서 존재 여부 확인 - 존재하지 않을 경우 false 반환")
    void isExist_ShouldReturnFalse_WhenBookDoesNotExist() throws IOException {
        // given
        String bookId = "1";
        when(repository.isExist(bookId)).thenReturn(false);

        // when
        boolean exists = elasticService.isExist(bookId);

        // then
        assertFalse(exists);
    }

//    @Test
//    @DisplayName("키워드 검색 - 검색 결과 반환")
//    void searchBookByWord_ShouldReturnBooks_WhenCalled() {
//        // given
//        String keyword = "test";
//        String sort = "asc";
//        List<SearchBook> books = Arrays.asList(
//                new SearchBook(),
//                new SearchBook());
//        when(repository.findByMultipleFields(keyword, sort)).thenReturn(books);
//
//        // when
//        List<SearchBook> result = elasticService.searchBookByWord(keyword, sort);
//
//        // then
//        assertEquals(books, result);
//    }

//    @Test
//    @DisplayName("JSON 파일 파싱 - 유효한 파일일 경우 도서 목록 반환")
//    void parseJsonFile_ShouldReturnBookList_WhenFileIsValid() throws IOException {
//        // given
//        String filePath = "test.json";
//        Resource resource = mock(Resource.class);
//        File file = mock(File.class);
//        List<SearchBook> books = Arrays.asList(new SearchBook(), new SearchBook());
//
//        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
//        when(resource.getFile()).thenReturn(file);
//        when(objectMapper.readValue(file, new TypeReference<List<SearchBook>>() {})).thenReturn(books);
//
//        // when
//        List<SearchBook> result = elasticService.parseJsonFile(filePath);
//
//        // then
//        assertEquals(books, result);
//    }
//
//    @Test
//    @DisplayName("전체 저장 - 파일 내 도서 모두 저장 성공")
//    void saveAll_ShouldSaveAllBooks_WhenCalled() throws IOException {
//        // given
//        String filePath = "test.json";
//        List<SearchBook> books = Arrays.asList(new SearchBook(), new SearchBook());
//
//        when(elasticService.parseJsonFile(filePath)).thenReturn(books);
//
//        // when
//        elasticService.saveAll(filePath);
//
//        // then
//        verify(repository, times(2)).save(any(SearchBook.class));
//    }
}
