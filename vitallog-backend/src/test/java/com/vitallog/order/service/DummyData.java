package com.vitallog.order.service;

import com.vitallog.order.dto.OrderItemDTO;
import com.vitallog.order.dto.OrderCreateRequestDTO;
import com.vitallog.supplement.entity.Supplement;

import java.util.List;
import java.util.UUID;

// 더미 데이터 클래스 (테스트 클래스 내부에 정의하거나 별도 클래스로 분리하여 사용)
// 더미 데이터 클래스 (테스트 클래스 내부에 정의하거나 별도 클래스로 분리하여 사용)
class DummyData {
    public static final UUID USER_UUID = UUID.randomUUID();
    public static final int NUT_NO_1 = 1;
    public static final int NUT_NO_2 = 2;

    /**
     * OrderItemDTO 객체를 생성자(Constructor)를 사용하여 생성합니다.
     * (OrderItemDTO에 Long nutNo, Integer quantity를 받는 생성자가 있다고 가정)
     */
    public static OrderItemDTO createItemDto(Long nutNo, Integer quantity) {
        // 가정: public OrderItemDTO(Long nutNo, Integer quantity) 생성자가 존재
        return new OrderItemDTO(nutNo, quantity);
    }

    /**
     * OrderRequestDTO 객체를 생성자(Constructor)를 사용하여 생성합니다.
     * (OrderRequestDTO에 모든 필드를 받는 생성자가 있다고 가정)
     */
    public static OrderCreateRequestDTO createOrderRequestDTO(List<OrderItemDTO> items) {
        // 가정: OrderRequestDTO에 모든 필드를 받는 생성자가 존재
        return new OrderCreateRequestDTO(
                USER_UUID.toString(),
                "서울시 테스트구 테스트로 123",
                items,
                "CREDIT_CARD",
                "빠른 배송 부탁드립니다."
        );
    }

    /**
     * Supplement 객체를 생성자(Constructor)를 사용하여 생성합니다.
     * (Supplement에 Long nutNo, int price, int stock을 받는 생성자가 있다고 가정)
     */
    public static Supplement createSupplement(Long nutNo, int price, int stock) {
        // 가정: Supplement 엔티티에 public Supplement(Long nutNo, int price, int stock) 생성자가 존재
        return new Supplement(nutNo, price, stock);
    }
}