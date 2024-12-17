package com.simsimbookstore.apiserver.carts.usercart.entity;


import com.simsimbookstore.apiserver.carts.cart.entity.Cart;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class UserCart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cart_id")
    private Long userCartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
