package com.simsimbookstore.apiserver.books.book.dto;


import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorResponsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookListResponse {

    private Long bookId;

    private String title;

    private LocalDate publicationDate;

    private BigDecimal price;

    private BigDecimal saleprice;

    private String publisher;

    private BookStatus bookStatus;

    private int quantity;

    private Long bookLikeId;

    private boolean isLiked;

    private List<BookContributorResponsDto> contributorRoleList;



}
