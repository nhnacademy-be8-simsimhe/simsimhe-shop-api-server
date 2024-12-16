package com.simsimbookstore.apiserver.bookset.booktag.domain;


import com.simsimbookstore.apiserver.bookset.book.domain.Book;
import com.simsimbookstore.apiserver.bookset.tag.domain.Tag;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book_tags")
public class BookTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_tag_id")
    private Long bookTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
