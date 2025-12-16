package com.vitallog.pay.service;

import com.vitallog.order.entity.Order;
import com.vitallog.pay.Entity.PayHistory;
import com.vitallog.pay.dto.TossRefundRequestDTO;
import com.vitallog.pay.dto.TossRefundResponseDTO;
import com.vitallog.pay.dto.TossConfirmResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class PayFacadeService {
    private final TossPaymentsService tossPaymentsService;
    private final PayHistoryService payHistoryService;

    @Transactional
    public TossConfirmResponseDTO confirmAndSavePayment(String paymentKey, String orderId, int amount) {
        // 결제 승인 요청
        TossConfirmResponseDTO responseDTO = tossPaymentsService.confirmPayment(paymentKey, orderId, amount);
        //결제 정보 저장
        payHistoryService.savePayHistory(
                responseDTO.getOrderId(),
                responseDTO.getPaymentKey(),
                OffsetDateTime.parse(responseDTO.getApprovedAt())
        );

        return responseDTO;
    }

    public TossRefundResponseDTO refundRequest(Order order, int totalRefundAmount, String reason) {
        // 결제 내역 조회 책임 위임 (PayHistoryService가 DB 접근)
        PayHistory payHistory = payHistoryService.getPayHistoryByOrder(order);

        // 토스 페이먼츠 환불 요청 dto 생성
        TossRefundRequestDTO refundRequest = new TossRefundRequestDTO(totalRefundAmount, reason);

        // 환불 dto 를 전달하며 환불 요청 로직 시도 (pgResponse 반환)
        return tossPaymentsService.refund(payHistory.getPaymentKey(), refundRequest);
    }
}
