package com.vitallog.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문 상세 ID")
    private Long orderDetailNo;

    @Comment("주문한 영양제 ID")
    private Long nutNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no")
    @Comment("주문")
    private Order order;

    @Comment("영양제 이름")
    private String nutName;

    @Comment("주문 수량")
    private int quantity;

    @Comment("수량 합산 가격")
    private int price;

    @Comment("레코드 생성 일시")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Comment("레코드 수정 일시")
    private LocalDateTime updatedAt;

    @Comment("레코드 삭제 일시")
    private LocalDateTime deletedAt;

    // 생성자에서 필요한 값만 받음
    public OrderDetail(Long nutNo, String productName, int quantity, int price) {
        this.nutNo = nutNo;
        this.nutName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    protected void setOrder(Order order) {  // public이 아님!
        this.order = order;
    }
}
