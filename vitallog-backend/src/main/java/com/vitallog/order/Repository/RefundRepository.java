package com.vitallog.order.Repository;

import com.vitallog.order.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundRepository extends JpaRepository<Refund, String> {
    /*
    * 서비스 로직에서 주문 상태를 바꿀 때
    * 환불 총액 == 주문 총금액 → "전체 환불 완료", 환불 총액 < 주문 총금액 → "부분 환불"
    * */
    @Query("select coalesce(sum(r.refundAmount), 0) " +
            "from Refund r " +
            "where r.order.orderNo = :orderNo")
    int sumByOrder(@Param("orderNo") String orderNo);
}

