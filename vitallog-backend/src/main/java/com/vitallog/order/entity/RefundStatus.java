package com.vitallog.order.entity;

public enum RefundStatus {

    // 1. 요청 (초기 상태)
    REQUESTED("환불 요청됨"),      // 사용자가 취소를 요청했거나 시스템이 취소 처리를 시작함

    // 2. 처리 중 (PG사 연동 단계)
    PROCESSING("PG사 처리 중"),    // 백엔드가 PG사에 취소 API를 호출하고 응답을 기다리는 중

    // 3. 완료 (성공 상태)
    COMPLETED("환불 완료"),        // PG사로부터 성공 응답을 받고 DB 상태 변경 및 재고 복구 완료

    // 4. 실패/거부 상태
    FAILED("환불 실패"),          // PG사 통신 오류, 결제 수단 문제 등으로 환불 처리 실패
    REJECTED("환불 거부됨");       // 관리자가 취소 요청을 거부함 (정책적 사유 등)

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
