package com.simsimbookstore.apiserver.carts.repository;

import com.simsimbookstore.apiserver.carts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long>, CartCustomRepository {

    @Modifying
    @Query("delete from Cart as c where c.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
