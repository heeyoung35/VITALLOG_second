package com.vitallog.pay.controller;

import com.vitallog.pay.dto.TossConfirmRequestDTO;
import com.vitallog.pay.dto.TossConfirmResponseDTO;
import com.vitallog.pay.service.PayFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pay")
@Tag(name = "결제 API", description = "결제 관련 API 입니다.")
public class PayController {
    private final PayFacadeService payFacadeService;

    // 결제 테스트 페이지
    @Operation(summary = "결제테스트 페이지", description = "결제 테스트용 페이지")
    @GetMapping("/test/payment")
    public String paymentTestPage() {
        return "payment"; // payment.html 반환
    }
    // 결제 요청 성공 후 결제 승인 api

    @Operation(summary = "결제 승인 및 결제 엔티티 저장 api", description = "토스 페이먼츠 결제 승인")
    @PostMapping("/confirm")
    public ResponseEntity<TossConfirmResponseDTO> confirmAndSavePayment(@RequestBody TossConfirmRequestDTO request) {

        TossConfirmResponseDTO response = payFacadeService.confirmAndSavePayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }
}
