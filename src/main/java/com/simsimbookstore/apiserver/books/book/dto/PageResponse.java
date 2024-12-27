package com.simsimbookstore.apiserver.books.book.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private List<T> data;
    private int currentPage;
    private int startPage;
    private int endPage;
    private int totalPage;
    private Long totalElements;


    public PageResponse<T> getPageResponse(int page, int maxPageButtons,
                                           Page<T> objectPage) {

        int startPage = (int) Math.max(1, objectPage.getNumber() - Math.floor((double) maxPageButtons / 2));
        int endPage = Math.min(startPage + maxPageButtons - 1, objectPage.getTotalPages());

        if (endPage - startPage + 1 < maxPageButtons) {
            startPage = Math.max(1, endPage - maxPageButtons + 1);
        }
        return PageResponse.<T>builder()
                .data(objectPage.getContent())
                .currentPage(page)
                .startPage(startPage)
                .endPage(endPage)
                .totalPage(objectPage.getTotalPages())
                .totalElements(objectPage.getTotalElements())
                .build();
    }
}
