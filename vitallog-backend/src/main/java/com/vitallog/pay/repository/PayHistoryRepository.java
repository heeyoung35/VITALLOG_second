package com.vitallog.pay.repository;

import com.vitallog.order.entity.Order;
import com.vitallog.pay.Entity.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayHistoryRepository extends JpaRepository<PayHistory,String> {
    Optional<PayHistory> findByOrder(Order order);
}
