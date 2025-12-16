package com.vitallog.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문 상품 상세 단위 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItemDTO {
    @NotNull(message = "상품 번호는 비워둘 수 없습니다.")
    private Long nutNo; // 영양제 No
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    @Max(value = 100, message = "수량은 최대 100개까지 주문 가능합니다.")
    private Integer quantity;
}