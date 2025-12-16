package com.vitallog.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderCreateResponseDTO {
    private String orderId;
    private int amount;
    private String orderName; //UI에 표시할 상품명
}
