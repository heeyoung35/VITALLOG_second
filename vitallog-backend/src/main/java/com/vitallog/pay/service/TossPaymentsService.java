package com.vitallog.pay.service;

import com.vitallog.pay.dto.TossRefundRequestDTO;
import com.vitallog.pay.dto.TossRefundResponseDTO;
import com.vitallog.pay.dto.TossConfirmResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TossPaymentsService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    public TossConfirmResponseDTO confirmPayment(String paymentKey, String orderId, int amount) {

        String url = "https://api.tosspayments.com/v1/payments/confirm";

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 토스 응답은 Map으로 받음
        Map<String, Object> response =
                restTemplate.postForObject(url, entity, Map.class);
        // 필요한 필드 추출해서 DTO 반환
        return convertToResponseDTO(response);
    }

    private TossConfirmResponseDTO convertToResponseDTO(Map<String, Object> map) {
        return TossConfirmResponseDTO.builder()
                .paymentKey((String) map.get("paymentKey"))
                .orderId((String) map.get("orderId"))
                .status((String) map.get("status"))
                .totalAmount((Integer) map.get("totalAmount"))
                .approvedAt((String) map.get("approvedAt"))
                .build();
    }

    public TossRefundResponseDTO refund(String paymentKey, TossRefundRequestDTO request) {

        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");   // ★ 중요: Secret Key 인증

        HttpEntity<TossRefundRequestDTO> entity = new HttpEntity<>(request, headers);

        Map<String, Object> response =
                restTemplate.postForObject(url, entity, Map.class);

        return convertToRefundResponseDTO(response);
    }

    private TossRefundResponseDTO convertToRefundResponseDTO(Map<String, Object> map) {
        List<Map<String, Object>> cancels = (List<Map<String, Object>>) map.get("cancels");
        Map<String, Object> cancelInfo = cancels != null && !cancels.isEmpty()
                ? cancels.get(0)
                : null;

        return TossRefundResponseDTO.builder()
                .paymentKey((String) map.get("paymentKey"))
                .orderId((String) map.get("orderId"))
                .status((String) map.get("status"))
                .pgRefundId(cancelInfo != null ? (String) cancelInfo.get("transactionKey") : null)
                .refundAmount(cancelInfo != null ? (Integer) cancelInfo.get("cancelAmount") : 0)
                .canceledAt(cancelInfo != null ? (String) cancelInfo.get("canceledAt") : null)
                .build();
    }
}
