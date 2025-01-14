package com.simsimbookstore.apiserver.books.book.dto;


import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorResponsDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDto {

    private Long bookId;

    private String thumbnailImage; // 썸네일 이미지 추가

    private String detailImage; // 디테일 이미지 추가

    private String title;

    private String description;

    private String bookIndex;

    private String publisher;

    private String isbn;

    private Long viewCount;

    private BigDecimal price;

    private BigDecimal saleprice;

    private LocalDate publicationDate;

    private int pages;

    private int quantity;

    private boolean isLiked;

    private BookStatus bookStatus;

    private boolean giftPackaging;

    private Long reviewCount; //리뷰 개수

    private Double scoreAverage; //리뷰 평점 없으면 0점



    private List<BookContributorResponsDto> contributorResponsDtos;
    private List<List<CategoryResponseDto>> categoryList;
    private List<TagResponseDto> tagList;

}
