package com.vitallog.order.controller;

import com.vitallog.common.response.ResponseDTO;
import com.vitallog.order.dto.*;
import com.vitallog.order.service.OrderFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "주문 API", description = "주문 관련 API")
@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/order")
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    /* 주문서 생성 */
    @Operation(summary = "주문서 생성", description = "payment.html에서 주문 시 호출하여 주문서 생성(재고 차감, 결제 준비, 결제 요청")
    @PostMapping
    public ResponseEntity<ResponseDTO<OrderCreateResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO request
    ) {

        // DTO가 유효성 검사를 통과했을 때 이 로그가 찍힘
        log.info("OrderCreateRequestDTO received: UserNo={}, Address={}, ItemsCount={}, PaymentMethod={}",
                request.getUserNo(),
                request.getAddress(),
                request.getItems().size(),
                request.getPaymentMethod());

        OrderCreateResponseDTO newOrder = orderFacadeService.createOrderAndPreparePayment(request);
        return new ResponseEntity<>(
                ResponseDTO.success(HttpStatus.CREATED, newOrder),
                HttpStatus.CREATED // HTTP 상태 코드 설정
        );
    }

    /* 주문 실패 시 주문 상태를 CANCELED로 변경 및 재고 롤백 */
    @Operation(summary = "주문 실패", description = "주문 실패 시 주문 상태를 CANCELED로 변경 및 재고 롤백")
    @PutMapping("/fail")
    public ResponseEntity<String> paymentFailWithOrderCancel(
            @RequestParam String orderId,
            @RequestParam String code,
            @RequestParam String message) {
        UUID userNo = UUID.fromString("3bbfa6c1-d0f7-11f0-a5cd-062bff1cb1bf"); //나중에 회원 완성되면 수정하기
        orderFacadeService.handlePaymentFailWithOrderCancel(orderId, code, message);

        return ResponseEntity.ok("결제 실패 처리 완료");
    }

    /* 주문 성공 시 주문 상태를 COMPLETED로 변경 및 장바구니 아이템 삭제 */
    @Operation(summary = "주문 성공", description = "주문 성공 시 주문 상태를 COMPLETED로 변경 및 장바구니 아이템 삭제")
    @PutMapping("/success")
    public ResponseEntity<String> paymentSuccessWithOrderCompleted(@RequestParam String orderNo) {
        orderFacadeService.paymentSuccessWithOrderCompleted(orderNo);
        String jsonResponse = "{\"message\": \"주문 처리 완료\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("charset", "UTF-8"); // Content-Type의 Charset을 UTF-8로 설정

        // HttpStatus.OK(200)와 JSON 문자열, 그리고 설정된 헤더를 반환
        return new ResponseEntity<>(jsonResponse, headers, HttpStatus.OK);
    }

    /* 사용자 주문서 전체 조회 */
    @Operation(summary = "사용자별 주문서 조회", description = "사용자 주문서 전체 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<List<OrderQueryResponseDTO>>> getAllOrders(@PathVariable String userId)
    // @AuthenticationPrincipal 등을 통해 JWT에서 사용자 ID 획득
//                                                                                , @AuthenticationPrincipal String jwtUserId) // 가정: JWT에서 추출된 ID
    {
//        // 1. **핵심 보안 검증:** JWT 사용자 ID와 Path Variable의 ID가 일치하는지 확인
//        if (!jwtUserId.equals(userId)) {
//            // 일치하지 않으면 접근 거부 예외 발생 (403 Forbidden 처리)
//            throw new AccessDeniedException("접근 권한이 없습니다. 다른 사용자의 정보에 접근할 수 없습니다.");
//        }
        List<OrderQueryResponseDTO> orderList = orderFacadeService.getAllOrders(userId);
        return ResponseEntity.ok(ResponseDTO.success(orderList));
    }

    /* 사용자 주문서 단일 조회 */
    @Operation(summary = "주문서 상세 조회", description = "사용자 주문서 상세 조회")
    @GetMapping
    public ResponseEntity<ResponseDTO<OrderQueryResponseDTO>> getOrder(@RequestParam String userId, @RequestParam String orderNo) {
        OrderQueryResponseDTO order = orderFacadeService.getOrder(orderNo);
        return ResponseEntity.ok(ResponseDTO.success(order));
    }

    /* 주문 취소 */
    @Operation(summary = "주문 취소", description = "주문No로 주문 취소 요청")
    @PostMapping("/{orderNo}/cancel")
    public ResponseEntity<ResponseDTO<Void>> cancelOrder(
            @PathVariable String orderNo,
            // JWT에서 추출한 userNo 또는 @AuthenticationPrincipal 사용
//            @RequestHeader("X-User-ID") String jwtUserId,
            @RequestBody OrderCancelRequestDTO request
    ) {
        //️ 1. 주문 소유권 및 권한 확인 로직 (IDOR 방지)
        String jwtUserId = "550e8400-e29b-41d4-a716-446655440000";
        // 2. 서비스 호출: 주문 번호, 사용자 ID, 취소 사유 전달
        orderFacadeService.requestCancelOrder(orderNo, jwtUserId, request.getReason(), request.getCancelItems());

        return new ResponseEntity<>(ResponseDTO.success(), HttpStatus.NO_CONTENT);
    }
}