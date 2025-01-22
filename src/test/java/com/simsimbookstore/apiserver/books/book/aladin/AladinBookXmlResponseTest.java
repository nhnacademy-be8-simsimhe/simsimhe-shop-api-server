package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AladinBookXmlResponseTest {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void testXmlDeserialization() throws JsonProcessingException {
        // Given: XML 데이터 샘플
        String xml = """
                    <item itemId="123456">
                        <title>Effective Java</title>
                        <link>https://www.aladin.co.kr/item/123456</link>
                        <author>Joshua Bloch</author>
                        <pubDate>2024-01-22</pubDate>
                        <description>Java 프로그래밍 베스트셀러</description>
                        <isbn>978-3-16-148410-0</isbn>
                        <isbn13>9783161484100</isbn13>
                        <priceSales>30000</priceSales>
                        <priceStandard>35000</priceStandard>
                        <mallType>BOOK</mallType>
                        <publisher>Addison-Wesley</publisher>
                        <categoryName>Programming</categoryName>
                        <cover>https://www.aladin.co.kr/images/effective-java.jpg</cover>
                        <salesPoint>5000</salesPoint>
                        <customerReviewRank>5</customerReviewRank>
                        <adult>false</adult>
                        <stockStatus>In Stock</stockStatus>
                    </item>
                """;

        // When: XML -> DTO 변환
        AladinBookXmlResponse response = xmlMapper.readValue(xml, AladinBookXmlResponse.class);

        // Then: 필드 값 검증
        assertEquals("123456", response.getItemId());
        assertEquals("Effective Java", response.getTitle());
        assertEquals("https://www.aladin.co.kr/item/123456", response.getLink());
        assertEquals("Joshua Bloch", response.getAuthor());
        assertEquals("2024-01-22", response.getPubDate());
        assertEquals("Java 프로그래밍 베스트셀러", response.getDescription());
        assertEquals("978-3-16-148410-0", response.getIsbn());
        assertEquals("9783161484100", response.getIsbn13());
        assertEquals(BigDecimal.valueOf(30000), response.getPriceSales());
        assertEquals(BigDecimal.valueOf(35000), response.getPriceStandard());
        assertEquals("BOOK", response.getMallType());
        assertEquals("Addison-Wesley", response.getPublisher());
        assertEquals("Programming", response.getCategoryName());
        assertEquals("https://www.aladin.co.kr/images/effective-java.jpg", response.getCover());
        assertEquals(5000, response.getSalesPoint());
        assertEquals(5, response.getCustomerReviewRank());
        assertFalse(response.isAdult());
        assertEquals("In Stock", response.getStockStatus());
    }

}