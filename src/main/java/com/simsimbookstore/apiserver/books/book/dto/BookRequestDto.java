package com.simsimbookstore.apiserver.books.book.dto;

import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookRequestDto {

    @NotBlank(message = "책 이름을 공백 없이 입력해주세요")
    @Length(max = 1000, message = "책 이름은 1000자를 초과할 수 없습니다")
    private String title;

    @NotBlank(message = "책 설명을 공백 없이 입력해주세요")
    @Length(max = 1000, message = "책 설명은 최대 1000자까지 입력 가능합니다")
    private String description;

    @NotBlank(message = "목차를 공백 없이 입력해주세요")
    @Length(max = 1000, message = "목차는 최대 1000자까지 입력 가능합니다")
    private String bookIndex;

    @NotBlank(message = "출판사를 공백 없이 입력해주세요")
    @Length(max = 50, message = "출판사 이름은 최대 50자까지 입력 가능합니다")
    private String publisher;

    @NotBlank(message = "ISBN을 공백 없이 입력해주세요")
    @Pattern(regexp = "\\d{13}", message = "ISBN은 13자리 숫자여야 합니다")
    private String isbn;

    @Min(value = 0, message = "수량은 0 이상이어야 합니다")
    private int quantity;

    @NotNull(message = "정가는 필수 입력 항목입니다")
    @Min(value = 0, message = "정가는 0 이상이어야 합니다")
    private BigDecimal price;

    @NotNull(message = "판매가는 필수 입력 항목입니다")
    @Min(value = 0, message = "판매가는 0 이상이어야 합니다")
    private BigDecimal saleprice;

    @NotNull(message = "출판일은 필수 입력 항목입니다")
    private LocalDate publicationDate;

    @Min(value = 1, message = "페이지 수는 1 이상이어야 합니다")
    private int pages;


    private BookStatus bookStatus;

    @NotNull
    private boolean giftPackaging; //true면 포장 가능

    private String thumbnailImage;


    private List<Long> contributoridList;

    private List<Long> categoryIdList;

    private List<Long> tagIdList;

}
