package com.simsimbookstore.apiserver.books.bookcontributor.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import jakarta.persistence.*;
import lombok.*;


@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book_contributors")
public class BookContributor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_contributor_id")
    private Long bookContributorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id")
    private Contributor contributor; //기여자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;


}
