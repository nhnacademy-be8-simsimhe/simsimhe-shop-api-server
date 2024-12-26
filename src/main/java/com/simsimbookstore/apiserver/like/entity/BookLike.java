package com.simsimbookstore.apiserver.like.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@Table(name = "book_likes")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class BookLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_like_id")
    private Long bookLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
}
