package com.vitallog.order.service;

import com.vitallog.cartitem.entity.CartItem;
import com.vitallog.cartitem.repository.CartItemRepository;
import com.vitallog.order.Repository.OrderRepository;
import com.vitallog.order.Repository.RefundRepository;
import com.vitallog.order.dto.*;
import com.vitallog.order.entity.*;
import com.vitallog.supplement.entity.Supplement;
import com.vitallog.supplement.repository.SupplementRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class OrderService {
    private final SupplementRepository supplementRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final CartItemRepository cartItemRepository;

    /* 주문 생성 로직 */
    @ExceptionHandler(IllegalArgumentException.class)
    @Transactional
    public Order createOrder(OrderCreateRequestDTO request /*, UUID userId*/) {
        // 요청한 사용자와 주문서의 사용자가 동일한지 확인하기
//        if(!request.getUserNo().equals(userId)){
//            new IllegalAccessError("주문자와 회원 ID가 일치하지 않습니다.");
//        };

        String userNo = request.getUserNo();

        // 1. 주문 요청 상품 ID 목록 추출
        List<Long> requestedNutNos = request.getItems().stream()
                .map(OrderItemDTO::getNutNo) // OrderItemDTO에 getNutNo()가 있다고 가정
                .collect(Collectors.toList());

        // 2. 장바구니 유효성 검사
        List<CartItem> existingCartItems = cartItemRepository.findByUserNoAndSupplementIds(userNo, requestedNutNos);
        log.info("DEBUG: 장바구니 조회 결과 개수: {}", existingCartItems.size()); // 0 출력
        log.info("DEBUG: request 조회 결과 개수: {}", request.getItems().size()); // 0 출력

        // 3. 주문 상품 개수와 장바구니 조회 결과 개수 비교 (모든 상품이 장바구니에 있는지 확인)
        if (existingCartItems.size() != request.getItems().size()) { // 0 != 2 (참)
            // ⭐️ 이 라인에서 정확히 IllegalArgumentException이 발생합니다.
            throw new IllegalArgumentException("주문 요청 상품 중 장바구니에 없는 상품이 포함되어 있습니다.");
        }

        // 총가격 계산을 위한 변수
        int total = 0;
        // 상품상세 저장할 리스트
        List<OrderDetail> tempDetails = new ArrayList<>();

        // 주문 유효성 검사, 재고 감소, 가격 계산을 하나의 루프에서 처리
        for (OrderItemDTO item : request.getItems()) {

            // 1-1. 장바구니 항목 찾기 (유효성 검사)
            CartItem cartItem = existingCartItems.stream()
                    .filter(ci -> ci.getSupplement().getNutNo().equals(item.getNutNo()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("장바구니 검증 로직 오류"));

            // 1-2. 주문 수량 > 장바구니 수량 확인
            if (item.getQuantity() > cartItem.getQuantity()) {
                throw new IllegalArgumentException("주문 수량이 장바구니 수량보다 많습니다. 장바구니를 다시 확인해주세요.");
            }

            // 1-3. 상품 조회 및 비관적 락 + 재고 확인
            Supplement supplement = supplementRepository.findByNutNoWithLock(item.getNutNo())
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));

            // 1-4. 재고 감소 (이미 락이 걸린 상태)
            supplement.decreaseStock(item.getQuantity());

            // 1-5. 가격 계산
            int itemTotal = supplement.getPrice() * item.getQuantity();
            total += itemTotal;

            // 1-6. OrderDetail 생성
            OrderDetail detail = new OrderDetail(
                    item.getNutNo(),
                    supplement.getNutName(),
                    item.getQuantity(),
                    itemTotal
            );
            tempDetails.add(detail);
        }

        // 주문서 생성
        Order newOrder = new Order(
                request.getUserNo(),
                total,
                OrderStatus.READY,
                request.getAddress(),
                request.getRequest(),
                request.getPaymentMethod()
        );

        // OrderDetail 저장 — orderId만 넣어서 저장
        for (OrderDetail item : tempDetails) {
            newOrder.addOrderDetail(item); // 연관관계 자동 설정하면서 상품 디테일 생성
        }

        // persist(order)
        orderRepository.save(newOrder); // cascade=ALL이면 detail도 함께 persist
        return newOrder;
    }
    public void findOrder(){
        orderRepository.findAll();
        System.out.println(orderRepository.findAll().getFirst().getRequest());
    }

    // 결제 실패 로직
    @Transactional
    public void handlePaymentFailWithOrderCancel(String orderId, String code, String message) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문이 존재하지 않습니다."));

        // 주문 상태 CANCELED로 변경
        order.updateStatus(OrderStatus.CANCELED);

        // 재고 롤백
        for (OrderDetail detail : order.getOrderDetails()) {
            Supplement supplement = supplementRepository.findById(detail.getNutNo())
                    .orElseThrow();
            supplement.increaseStock(detail.getQuantity());
        }

        // 로그 남기기 (히스토리 테이블 없어도 충분)
        log.warn("결제 실패: orderId={}, code={}, message={}", orderId, code, message);
    }

    // 결제 완료 후 주문 상태 PAID 변경하는 로직
    @Transactional
    public Order paymentSuccessWithOrderCompleted(String orderNo) {
        Order order = orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문이 존재하지 않습니다."));
        order.updateStatus(OrderStatus.PAID);
        return order;
    }

    // 사용자의 모든 주문 조회
    public List<OrderQueryResponseDTO> getAllOrders(String userId) {
        // 1. Fetch Join이 적용된 메서드 호출 (쿼리 1회만 발생!)
        // DB에서 Order 10개와 OrderDetail 30개를 단 1번의 JOIN 쿼리로 가져옵니다.
        List<Order> orderList = orderRepository.findByUserNoWithDetails(userId);

        if (orderList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Stream을 사용하여 엔티티 리스트를 DTO 리스트로 변환
        return orderList.stream()
                .map(this::mapToOrderQueryResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderQueryResponseDTO getOrder(String orderNo) {
        Order order = orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
        return mapToOrderQueryResponseDTO(order);
    }

    // --- 매핑 헬퍼 메서드 ---
    private OrderQueryResponseDTO mapToOrderQueryResponseDTO(Order order) {
        // OrderDetail 리스트를 OrderItemQueryDTO 리스트로 변환 (쿼리 발생 없음)
        List<OrderItemQueryDTO> itemDTOs = order.getOrderDetails().stream()
                .map(this::mapToOrderItemQueryDTO)
                .collect(Collectors.toList());

        // 최종 DTO 생성
        return new OrderQueryResponseDTO(
                order.getOrderNo(),
                order.getUserNo(),
                order.getTotalPrice(),
                order.getAddress(),
                itemDTOs, // 변환된 Item DTO 리스트 할당
                order.getPaymentMethod(),
                order.getRequest(),
                order.getStatus()
        );
    }

    private OrderItemQueryDTO mapToOrderItemQueryDTO(OrderDetail detail) {
        return new OrderItemQueryDTO(
                detail.getNutName(),
                detail.getQuantity(),
                detail.getPrice()
        );
    }

    /* 주문 취소 */
//    @Transactional
//    public void cancelOrder(String orderNo, String userNo, String reason, List<OrderDetailCancelRequestDTO> cancelItems) {
//        Order order = orderRepository.findById(orderNo)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
//
//        int totalRefundAmount = 0;
//
//        // 1. 주문 상세(OrderDetail)와 대조하며 취소 로직 수행
//        for (OrderDetailCancelRequestDTO cancelReq : cancelItems) {
//            // 2. 요청된 detailId로 주문 상세 엔티티를 정확하게 찾습니다.
//            // findFirst()는 하나의 고유한 detailId에 대응하는 OrderDetail을 찾으므로 정확합니다.
//            // 해당 nutNo를 가진 OrderDetail 항목을 찾습니다.
//            OrderDetail detailToCancel = order.getOrderDetails().stream()
//                    .filter(d -> d.getNutNo().equals(cancelReq.getNutNo()))
//                    .findFirst()
//                    .orElseThrow(() -> new IllegalArgumentException("주문 상세 항목을 찾을 수 없습니다."));
//
//            // 3. 취소 금액 계산 및 누적
//            int itemPricePerUnit = detailToCancel.getPrice() / detailToCancel.getQuantity();
//            int itemRefundAmount = itemPricePerUnit * cancelReq.getQuantity();
//            totalRefundAmount += itemRefundAmount;
//
//            // 4. 재고 롤백
//            Supplement supplement = supplementRepository.findById(Math.toIntExact(detailToCancel.getNutNo()))
//                    .orElseThrow();
//            supplement.increaseStock(cancelReq.getQuantity());
//            supplementRepository.save(supplement);
//        }
//
//        // PG 환불
//        PayHistory payHistory = payHistoryRepository.findByOrder(order)
//                .orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제 정보가 없습니다."));
//
//        TossRefundRequestDTO refundRequest = new TossRefundRequestDTO(totalRefundAmount, reason);
//
//        TossRefundResponseDTO pgResponse;
//        try {
//            pgResponse = tossPaymentsService.refund(payHistory.getPaymentKey(), refundRequest);
//
//            if (!"CANCELED".equals(pgResponse.getStatus())) {
//                throw new IllegalStateException("PG 환불 실패, status=" + pgResponse.getStatus());
//            }
//
//        } catch (HttpClientErrorException e) {
//            // Toss 에러 응답 JSON 파싱해서 message 뽑고 싶으면 여기서 처리
//            throw new IllegalStateException("PG 환불 실패: " + e.getResponseBodyAsString(), e);
//        }
//
//        // Refund 엔티티 생성
//        Refund refund = new Refund(
//                null, // refundNo = @PrePersist 에서 UUID 자동 생성
//                order,
//                totalRefundAmount,
//                pgResponse.getPgRefundId(),
//                reason,
//                RefundStatus.COMPLETED,
//                null
//        );
//        refundRepository.save(refund);
//
//        // 주문 상태 변경
//        int refundedSum = refundRepository.sumByOrder(order.getOrderNo());
//
//        if (refundedSum >= order.getTotalPrice()) {
//            order.changeStatus(OrderStatus.CANCELED);
//        } else {
//            order.changeStatus(OrderStatus.PARTIALLY_CANCELED);
//        }
//
//        orderRepository.save(order);
//    }

    /* 주문 번호로 주문 확인하기 */
    public Order validateAndGetOrder(String orderNo, String userNo) {
        return orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
    }

    /* 2. 항목별 처리, 금액 계산, 재고 복구 메서드 (OrderService/StockService)
       - OrderService가 금액 계산을 하고, 재고 롤백은 SupplementService에 위임합니다.*/
    public int processItemsAndRestoreStock(Order order, List<OrderDetailCancelRequestDTO> cancelItems) {
        int totalRefundAmount = 0;

        // 1. 주문 상세(OrderDetail)와 대조하며 취소 로직 수행
        for (OrderDetailCancelRequestDTO cancelReq : cancelItems) {
            // 2. 요청된 detailId로 주문 상세 엔티티를 정확하게 찾습니다.
            // findFirst()는 하나의 고유한 detailId에 대응하는 OrderDetail을 찾으므로 정확합니다.
            // 해당 nutNo를 가진 OrderDetail 항목을 찾습니다.
            OrderDetail detailToCancel = order.getOrderDetails().stream()
                    .filter(d -> d.getNutNo().equals(cancelReq.getNutNo()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("주문 상세 항목을 찾을 수 없습니다."));

            // 3. 취소 금액 계산 및 누적
            int itemPricePerUnit = detailToCancel.getPrice() / detailToCancel.getQuantity();
            int itemRefundAmount = itemPricePerUnit * cancelReq.getQuantity();
            totalRefundAmount += itemRefundAmount;

            // 4. 재고 롤백
            Supplement supplement = supplementRepository.findById(detailToCancel.getNutNo())
                    .orElseThrow();
            supplement.increaseStock(cancelReq.getQuantity());
            supplementRepository.save(supplement);
        }
        return totalRefundAmount;
    }
    /* 환불 완료 후 DB 저장 */
    public void createAndSaveRefund(Order order, Integer refundAmount, String pgRefundId, String reason) {
        // Refund 엔티티 생성
        Refund refund = new Refund(
                null, // refundNo = @PrePersist 에서 UUID 자동 생성
                order,
                refundAmount,
                pgRefundId,
                reason,
                RefundStatus.COMPLETED,
                null
        );
        refundRepository.save(refund);

        // 주문 상태 변경
        int refundedSum = refundRepository.sumByOrder(order.getOrderNo());

        if (refundedSum >= order.getTotalPrice()) {
            order.changeStatus(OrderStatus.CANCELED);
        } else {
            order.changeStatus(OrderStatus.PARTIALLY_CANCELED);
        }

        orderRepository.save(order);
    }
}
