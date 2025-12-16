package com.vitallog.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(length = 36)
    @Comment("주문 번호")
    private String orderNo;

    @Comment("주문 유저 번호")
    private String userNo;

    @Comment("총 가격")
    private int totalPrice;

    @Comment("주문 상태")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Comment("주소")
    private String address;

    @Comment("요청사항")
    private String request;

    @Comment("결제 수단")
    private String paymentMethod;

    @Comment("레코드 생성 일시")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Comment("레코드 수정 일시")
    private LocalDateTime updatedAt;

    @Comment("레코드 삭제 일시")
    private LocalDateTime deletedAt;

    // orphanRemoval : 컬렉션에서 삭제돼도 삭제
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this); // 여기서만 setter 사용 (private 가능)
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    // 생성자
    public Order(String userNo, int totalPrice, OrderStatus status,
                 String address, String request, String paymentMethod) {
        this.userNo = userNo;
        this.totalPrice = totalPrice;
        this.status = status;
        this.address = address;
        this.request = request;
        this.paymentMethod = paymentMethod;
    }

    @PrePersist
    public void generateIdAndTime() {
        if (orderNo == null) {
            orderNo = UUID.randomUUID().toString();
        }
        // createdAt 최초 생성 시에만 값 설정
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}
