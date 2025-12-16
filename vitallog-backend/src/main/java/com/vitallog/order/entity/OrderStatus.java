package com.vitallog.order.entity;

public enum OrderStatus {
    READY("주문서 생성(결제 전)"),
    PAID("결제 완료"),
    COMPLETED("구매 확정"),
    CANCELED("주문 취소"),
    PARTIALLY_CANCELED("부분 취소");


    // 1. 각 상수별로 고유한 데이터("설명")를 정의
    private String description;

    private OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
