package com.vitallog.cartitem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartItemRequestDTO {

    private Long nutNo;
    private int cartNo;
    private int quantity;
    private LocalDateTime createdAt;

}
