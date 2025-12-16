package com.vitallog.cartitem.entity;

import com.vitallog.cart.Entity.Cart;
import com.vitallog.supplement.entity.Supplement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_Item")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItem_no")
    private Integer cartItemNo;

    @ManyToOne
    @JoinColumn(name = "nutNo")
    private Supplement supplement;

    @ManyToOne
    @JoinColumn(name = "cartNo")
    private Cart cart;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @PrePersist
    protected void createdAt() {
        this.createdAt = LocalDateTime.now();
    }


    public void setQuantity(Integer quantity) {
        this.quantity = quantity;

    }
}
