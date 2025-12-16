package com.vitallog.pay.service;

import com.vitallog.order.Repository.OrderRepository;
import com.vitallog.order.entity.Order;
import com.vitallog.pay.Entity.PayHistory;
import com.vitallog.pay.repository.PayHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Service
public class PayHistoryService {
    private final PayHistoryRepository payHistoryRepository;
    private final OrderRepository OrderRepository;

    /* 결제 정보 저장 메서드 */
    @Transactional
    public void savePayHistory(String orderNo, String paymentKey, OffsetDateTime approvedAt) {
        Order order = OrderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));
        PayHistory newPayHistory = new PayHistory(order, paymentKey, approvedAt);

        payHistoryRepository.save(newPayHistory);
    }

    public PayHistory getPayHistoryByOrder(Order order) {
        // PayHistoryService 내부에서 Repository를 사용하여 조회
        return payHistoryRepository.findByOrder(order)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제 정보가 없습니다."));
    }
}
