package com.vitallog.order.Repository;

import com.vitallog.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserNo(String userNo);

    /**
     * 특정 사용자(userNo)의 모든 주문 엔티티를 조회하면서,
     * 연관된 OrderItem 리스트(items)를 Fetch Join을 사용하여 함께 즉시 로딩합니다.
     * * @param userNo 조회할 사용자 번호
     * @return Order 엔티티 리스트 (OrderItem이 즉시 로딩된 상태)
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.userNo = :userNo")
    List<Order> findByUserNoWithDetails(@Param("userNo") String userNo);

    // 참고: 기존의 findByUserNo(String userNo) 메서드는 그대로 두거나,
    // 이 Fetch Join 메서드로 대체하여 사용하시면 됩니다.
}
