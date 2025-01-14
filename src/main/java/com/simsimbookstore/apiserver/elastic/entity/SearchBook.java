package com.simsimbookstore.apiserver.elastic.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Document(indexName = "simsimhe-books")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class SearchBook {
    @Id
    Long id;
    String title;
    String description;
    String author;
    String bookImage;
    List<String> tags;
    String publishedAt;
    long salePrice;
    long bookSellCount;
    long reviewCount;

}
