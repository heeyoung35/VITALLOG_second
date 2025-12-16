package com.vitallog.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 주문 단위 DTO
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "주문서 생성 요청 DTO")
public class OrderCreateRequestDTO {
    @Schema(description = "사용자 No", example = "userNo")
    @NotNull(message = "사용자 번호는 필수입니다.") // UUID는 문자열도 아니고 공백 개념이 없어서 Not Null 적용해야한다.
    private String userNo;

    @Schema(description = "배송지", example = "address")
    @NotBlank(message = "주소는 비워둘 수 없습니다.")
    private String address; // 배송지

    @Schema(description = "주문 상품 목록", example = "items")
    @NotEmpty(message = "주문 상품은 하나 이상 존재해야 합니다.") // 비어있는 리스트를 막는 것은 NotEmpty
    private List<OrderItemDTO> items; // 주문 상품 리스트

    @Schema(description = "결제 수단", example = "paymentMethod")
    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod; // 결제 수단

    @Schema(description = "요청 사항", example = "request")
    private String request; // 주문 요청
}
