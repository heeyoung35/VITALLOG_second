package com.vitallog.order.dto;

import com.vitallog.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderQueryResponseDTO {
    private String orderNo;
    private String userNo;
    private int totalPrice;
    private String address; // 배송지
    private List<OrderItemQueryDTO> items; // 주문 상품 리스트
    private String paymentMethod; // 결제 수단
    private String request; // 요청사항
    private OrderStatus status;
}
