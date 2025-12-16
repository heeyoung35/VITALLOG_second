package com.vitallog.pay.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vitallog.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class PayHistory {
    @Id
    @Column(length = 36)
    @Comment("결제 Id")
    private String payHistoryNo;

    @OneToOne
    @JoinColumn(name = "order_no", unique = true)
    @Comment("주문 Id")
    private Order order;

    @Column(name = "payment_key")
    @Comment("결제 KEY")
    private String paymentKey;

    @Comment("결제 승인 일시")
    @Column(name = "approved_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime approvedAt;

    @Comment("레코드 삭제 일시")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public PayHistory(Order order, String paymentKey, OffsetDateTime approvedAt) {
        this.payHistoryNo = UUID.randomUUID().toString();
        this.order = order;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
    }
}



