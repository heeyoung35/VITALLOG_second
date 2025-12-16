package com.vitallog.order.service;

import com.vitallog.cartitem.repository.CartItemRepository;
import com.vitallog.order.dto.OrderCreateRequestDTO;
import com.vitallog.order.dto.OrderCreateResponseDTO;
import com.vitallog.order.dto.OrderDetailCancelRequestDTO;
import com.vitallog.order.dto.OrderQueryResponseDTO;
import com.vitallog.order.entity.Order;
import com.vitallog.order.entity.OrderDetail;
import com.vitallog.pay.dto.TossRefundResponseDTO;
import com.vitallog.pay.service.PayFacadeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service // 비즈니스 흐름(Flow)과 결제 준비만 전담
public class OrderFacadeService {

    private final OrderService orderService;
    private final PayFacadeService payFacadeService;
    private final CartItemRepository cartItemRepository;

    /* 주문 생성 및 결제 준비 로직 */
    public OrderCreateResponseDTO createOrderAndPreparePayment(@Valid OrderCreateRequestDTO request) {
        Order newOrder = orderService.createOrder(request);
        String orderName = newOrder.getOrderDetails().getFirst().getNutName() + " 외 " + newOrder.getOrderDetails().size() + "개";
        return new OrderCreateResponseDTO(
                newOrder.getOrderNo(),
                newOrder.getTotalPrice(),
                orderName
        );
    }

    public void handlePaymentFailWithOrderCancel(String orderId, String code, String message) {
        orderService.handlePaymentFailWithOrderCancel(orderId, code, message);
        //장바구니 추가로직
        //반환형 다시 만들기
    }

    @Transactional
    public void paymentSuccessWithOrderCompleted(String orderNo) {
        // 1. 주문 조회 및 상태 업데이트
        Order order = orderService.paymentSuccessWithOrderCompleted(orderNo);
        // 2. 주문된 상품 ID(nutNo) 목록 추출
        List<Long> orderedNutNos = order.getOrderDetails().stream()
                .map(OrderDetail::getNutNo) // OrderItem에서 Supplement의 nutNo를 가져오는 메서드를 사용해야 함 (가정)
                .toList();
        // 3. 사용자 ID 추출(Cart를 조회하기 위한 선행 작업)
        String userNo = order.getUserNo();

        // 4. 장바구니 항목 일괄 삭제 (단일 쿼리 실행)
        if (!orderedNutNos.isEmpty() && userNo != null) {
            cartItemRepository.deleteByUserIdAndSupplementIds(userNo, orderedNutNos);
        }
    }

    public List<OrderQueryResponseDTO> getAllOrders(String userId) {
        return orderService.getAllOrders(userId);
    }

    public OrderQueryResponseDTO getOrder(String orderNo) {
        return orderService.getOrder(orderNo);
    }

    /* 주문 취소 */
    @Transactional
    public void requestCancelOrder(String orderNo, String userNo, String reason, List<OrderDetailCancelRequestDTO> cancelItems) {
        // 1. 주문 엔티티 조회 및 검증 (OrderService의 역할)
        Order order = orderService.validateAndGetOrder(orderNo, userNo);

        // 2. 항목별 처리, 금액 계산, 재고 복구 (OrderService/StockService)
        // OrderService가 금액 계산을 하고, 재고 롤백은 SupplementService에 위임합니다.
        int totalRefundAmount = orderService.processItemsAndRestoreStock(order, cancelItems);

        TossRefundResponseDTO pgResponse = payFacadeService.refundRequest(order, totalRefundAmount, reason);

        // 4. Refund 엔티티 생성 및 저장 (OrderService)
        // PG 정보(pgRefundId)를 받아 Refund 기록을 생성합니다.
        orderService.createAndSaveRefund(
                order,
                pgResponse.getRefundAmount(),
                pgResponse.getPgRefundId(),
                reason
        );

    }
}
