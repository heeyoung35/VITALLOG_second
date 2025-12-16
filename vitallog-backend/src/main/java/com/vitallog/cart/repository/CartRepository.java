package com.vitallog.cart.repository;

import com.vitallog.cart.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser_UserNo(String userNo);


}
