package com.vitallog.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderItemQueryDTO {
    private String nutName;
    private int quantity;
    private int price;
}
