package com.vitallog.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    @Id
    @Column(length = 36)
    @Comment("환불 번호")
    private String refundNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no", referencedColumnName = "orderNo")
    private Order order;

    @Column(nullable = false)
    private int refundAmount; // 실제 환불 금액

    @Column(nullable = false, length = 100)
    private String pgRefundId; // PG사 환불 거래 고유 ID (추적용)

    @Column(length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status; // 환불 상태 (REQUESTED, COMPLETED 등)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 환불 요청

    @PrePersist
    protected void onCreate() {

        // createdAt 최초 생성 시에만 값 설정
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        // refundNo 없으면 UUID 자동 생성
        if (this.refundNo == null) {
            this.refundNo = UUID.randomUUID().toString();
        }
    }
}
