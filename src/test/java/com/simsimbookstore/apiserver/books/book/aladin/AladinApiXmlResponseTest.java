package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AladinApiXmlResponseTest {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void testXmlDeserialization() throws JsonProcessingException {
        // Given: XML 데이터 샘플
        String xml = """
                    <AladinApiXmlResponse>
                        <title>Aladin API Response</title>
                        <link>https://www.aladin.co.kr</link>
                        <logo>https://www.aladin.co.kr/logo.png</logo>
                        <pubDate>2024-01-22</pubDate>
                        <totalResults>100</totalResults>
                        <startIndex>1</startIndex>
                        <itemsPerPage>10</itemsPerPage>
                        <query>Java</query>
                        <version>1.0</version>
                        <searchCategoryId>123</searchCategoryId>
                        <searchCategoryName>Programming</searchCategoryName>
                        <item>
                            <title>Effective Java</title>
                            <author>Joshua Bloch</author>
                            <publisher>Addison-Wesley</publisher>
                        </item>
                        <item>
                            <title>Java Concurrency in Practice</title>
                            <author>Brian Goetz</author>
                            <publisher>Addison-Wesley</publisher>
                        </item>
                    </AladinApiXmlResponse>
                """;

        // When: XML -> DTO 변환
        AladinApiXmlResponse response = xmlMapper.readValue(xml, AladinApiXmlResponse.class);

        // Then: 필드 값 검증
        assertEquals("Aladin API Response", response.getTitle());
        assertEquals("https://www.aladin.co.kr", response.getLink());
        assertEquals("https://www.aladin.co.kr/logo.png", response.getLogo());
        assertEquals("2024-01-22", response.getPubDate());
        assertEquals(100, response.getTotalResults());
        assertEquals(1, response.getStartIndex());
        assertEquals(10, response.getItemsPerPage());
        assertEquals("Java", response.getQuery());
        assertEquals("1.0", response.getVersion());
        assertEquals(123, response.getSearchCategoryId());
        assertEquals("Programming", response.getSearchCategoryName());

        // 아이템 리스트 검증
        List<AladinBookXmlResponse> items = response.getItems();
        assertNotNull(items);
        assertEquals(2, items.size());

        assertEquals("Effective Java", items.get(0).getTitle());
        assertEquals("Joshua Bloch", items.get(0).getAuthor());
        assertEquals("Addison-Wesley", items.get(0).getPublisher());

        assertEquals("Java Concurrency in Practice", items.get(1).getTitle());
        assertEquals("Brian Goetz", items.get(1).getAuthor());
        assertEquals("Addison-Wesley", items.get(1).getPublisher());
    }



}