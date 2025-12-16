package com.vitallog.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossRefundResponseDTO {
    private String paymentKey;
    private String orderId;
    private String status;
    private List<CancelInfo> cancels;
    private Integer refundAmount;
    private String canceledAt;


    private String pgRefundId;  // ★ 추가

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelInfo {
        private int cancelAmount;
        private String cancelReason;
        private String canceledAt;
        private String transactionKey;
    }

    public boolean isSuccess() {
        return cancels != null && !cancels.isEmpty();
    }

    public String getPgRefundId() {
        return cancels != null && !cancels.isEmpty()
                ? cancels.get(0).getTransactionKey()
                : pgRefundId; // 없어도 괜찮음
    }
}
