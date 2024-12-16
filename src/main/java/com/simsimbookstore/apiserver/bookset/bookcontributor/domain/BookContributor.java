package com.simsimbookstore.apiserver.bookset.bookcontributor.domain;


import com.simsimbookstore.apiserver.bookset.book.domain.Book;
import com.simsimbookstore.apiserver.bookset.contributor.domain.Contributor;
import com.simsimbookstore.apiserver.bookset.contributorrole.domain.ContributorRole;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_role_id")
    private ContributorRole contributorRole;


}
