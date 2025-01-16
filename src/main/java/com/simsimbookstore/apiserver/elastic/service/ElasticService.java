package com.simsimbookstore.apiserver.elastic.service;




import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.exception.FileParsingException;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookDeleteFailedException;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookExistenceCheckFailedException;
import com.simsimbookstore.apiserver.elastic.exception.SearchBookSaveFailedException;
import com.simsimbookstore.apiserver.elastic.repository.CustomRepository;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticService {
    private final CustomRepository repository;

    private final ResourceLoader resourceLoader;

    public void createBook(SearchBook book){

        if (isExist(String.valueOf(book.getId()))){
            throw new AlreadyExistException("해당 도서가 인덱스에 이미 존재합니다");
        }

        try {
            repository.save(book);
        } catch (IOException e) {
            throw new SearchBookSaveFailedException("해당 도서를 인덱싱하는데 실패했습니다.", e);
        }
    }

    public void deleteBook(String id){

        if (!isExist(id)){
            throw new NotFoundException("해당 도서가 존재하지 않습니다");
        }

        try {
            repository.delete(id);
        } catch (IOException e) {
            throw new SearchBookDeleteFailedException("인덱스에서 도서를 삭제하는 중 오류가 발생했습니다.",e);
        }
    }

    public boolean isExist(String id) {
        try {
            return repository.isExist(id);
        } catch (IOException e) {
            throw new SearchBookExistenceCheckFailedException("도서 존재 여부 확인에 실패했습니다",e);
        }
    }


    public PageResponse<SearchBook> searchBookByWord(String keyword, String sort, int page){
        Page<SearchBook> responses = repository.findByMultipleFields(keyword,sort, page);
        return getPageResponse(page, responses);
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
            throw new FileParsingException("Json 파일을 파싱하는 중 오류가 발생했습니다. 파일 경로: " + filePath);
        }

        for (SearchBook book : books){
           createBook(book);
        }

    }


    private PageResponse<SearchBook> getPageResponse(int page,
                                                           Page<SearchBook> bookPage) {
        int maxPageButtons = 10;
        int startPage = (int) Math.max(1, bookPage.getNumber() - Math.floor((double) maxPageButtons / 2));
        int endPage = Math.min(startPage + maxPageButtons - 1, bookPage.getTotalPages());

        if (endPage - startPage + 1 < maxPageButtons) {
            startPage = Math.max(1, endPage - maxPageButtons + 1);
        }

        return PageResponse.<SearchBook>builder()
                .data(bookPage.getContent())
                .currentPage(page)
                .startPage(startPage)
                .endPage(endPage)
                .totalPage(bookPage.getTotalPages())
                .totalElements(bookPage.getTotalElements())
                .build();
    }



}
