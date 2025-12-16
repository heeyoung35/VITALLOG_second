package com.vitallog.pay.dto;

import lombok.Getter;

@Getter
public class TossConfirmRequestDTO {
    private String paymentKey;
    private String orderId;
    private int amount;
}