package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AladinApiXmlResponse {

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "link")
    private String link;

    @JacksonXmlProperty(localName = "logo")
    private String logo;

    @JacksonXmlProperty(localName = "pubDate")
    private String pubDate;

    @JacksonXmlProperty(localName = "totalResults")
    private int totalResults;

    @JacksonXmlProperty(localName = "startIndex")
    private int startIndex;

    @JacksonXmlProperty(localName = "itemsPerPage")
    private int itemsPerPage;

    @JacksonXmlProperty(localName = "query")
    private String query;

    @JacksonXmlProperty(localName = "version")
    private String version;

    @JacksonXmlProperty(localName = "searchCategoryId")
    private int searchCategoryId;

    @JacksonXmlProperty(localName = "searchCategoryName")
    private String searchCategoryName;

    @JacksonXmlElementWrapper(useWrapping = false) // <item> 태그는 배열 형태
    @JacksonXmlProperty(localName = "item") // <item> 태그를 매핑
    private List<AladinBookXmlResponse> items;
}