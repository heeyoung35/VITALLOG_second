package com.vitallog.cartitem.dto;

import com.vitallog.cart.Entity.Cart;
import com.vitallog.supplement.entity.Supplement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartItemResponseDTO {

    private Integer cartItemNo;
    private Long nutNo;
    private String nutName;
    private int price;
    private int cartNo;
    private int quantity;
    private LocalDateTime createdAt;

    public void setCart(Cart cart) {
        if (cart != null) {
            this.cartNo = cart.getCartNo();
        }
    }

    public void setSupplement(Supplement supplement) {
        if (supplement != null) {
            this.nutNo = supplement.getNutNo();
            this.nutName = supplement.getNutName();
            this.price = supplement.getPrice();
        }
    }

}
