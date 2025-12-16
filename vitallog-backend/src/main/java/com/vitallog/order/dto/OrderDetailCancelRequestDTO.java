package com.vitallog.order.dto;

import lombok.Getter;

@Getter
public class OrderDetailCancelRequestDTO {
    private Long nutNo;    // 취소할 상품 번호 (order_detail의 nut_no)
    private int quantity;    // 취소할 수량
}
