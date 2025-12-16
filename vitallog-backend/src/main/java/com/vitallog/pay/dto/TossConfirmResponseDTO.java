package com.vitallog.pay.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossConfirmResponseDTO {
    private String paymentKey;
    private String orderId;
    private String status;
    private Integer totalAmount;
    private String approvedAt;
}

