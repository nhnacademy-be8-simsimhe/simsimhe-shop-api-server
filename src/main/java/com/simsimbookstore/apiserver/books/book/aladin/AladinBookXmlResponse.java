package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true) // 매핑되지 않은 필드를 무시
@Getter
@Setter
public class AladinBookXmlResponse {

    @JacksonXmlProperty(localName = "itemId", isAttribute = true) // itemId는 속성
    private String itemId;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "link")
    private String link;

    @JacksonXmlProperty(localName = "author")
    private String author;

    @JacksonXmlProperty(localName = "pubDate")
    private String pubDate;

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "isbn")
    private String isbn;

    @JacksonXmlProperty(localName = "isbn13")
    private String isbn13;

    @JacksonXmlProperty(localName = "priceSales")
    private BigDecimal priceSales;

    @JacksonXmlProperty(localName = "priceStandard")
    private BigDecimal priceStandard;

    @JacksonXmlProperty(localName = "mallType") // mallType 추가
    private String mallType;

    @JacksonXmlProperty(localName = "publisher")
    private String publisher;

    @JacksonXmlProperty(localName = "categoryName")
    private String categoryName;

    @JacksonXmlProperty(localName = "cover")
    private String cover;

    @JacksonXmlProperty(localName = "salesPoint")
    private int salesPoint;

    @JacksonXmlProperty(localName = "customerReviewRank")
    private int customerReviewRank;

    @JacksonXmlProperty(localName = "adult")
    private boolean adult;

    @JacksonXmlProperty(localName = "stockStatus") // stockStatus 필드 추가
    private String stockStatus;
}