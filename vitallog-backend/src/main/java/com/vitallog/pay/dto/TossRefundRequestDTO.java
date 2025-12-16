package com.vitallog.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TossRefundRequestDTO {
    private int cancelAmount;
    private String cancelReason;
}
