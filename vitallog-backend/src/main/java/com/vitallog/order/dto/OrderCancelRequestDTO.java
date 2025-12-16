package com.vitallog.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCancelRequestDTO {
    private String reason; // 취소 사유
    private List<OrderDetailCancelRequestDTO> cancelItems; // 취소 상품 목록
}
