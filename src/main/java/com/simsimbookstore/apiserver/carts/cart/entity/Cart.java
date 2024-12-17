package com.simsimbookstore.apiserver.carts.cart.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
}
