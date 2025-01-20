package com.simsimbookstore.apiserver.books.book.dto;


import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorResponsDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookListResponse {

    private Long bookId;

    private String imagePath;

    private String title;

    private LocalDate publicationDate;

    private BigDecimal price;

    private BigDecimal saleprice;

    private String publisher;

    private BookStatus bookStatus;

    private int quantity;

    private Long bookLikeId;

    private boolean isLiked;

    private boolean giftPackaging;

    private Long reviewCount; //리뷰 개수

    private List<BookContributorResponsDto> contributorList;


}
