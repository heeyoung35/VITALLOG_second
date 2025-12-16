package com.vitallog.cartitem.repository;

import com.vitallog.cartitem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart_CartNo(Integer cartNo);
    Optional<CartItem> findByCart_CartNoAndSupplement_NutNo(Integer cartNo, Long nutNo);
    /**
     * 주문된 상품 목록과 사용자 정보를 기반으로 장바구니 항목을 일괄 삭제합니다.
     * * @param userNo 장바구니 소유자 ID (Cart 엔티티를 통해 접근)
     * @param nutNos 주문된 상품 ID 목록 (Supplement 엔티티를 통해 접근)
     */
    // CartItemRepository의 JPQL (수정된 버전)
    @Modifying
    @Query("DELETE FROM CartItem ci " +
            "WHERE ci.cart.user.userNo = :userNo AND ci.supplement.nutNo IN :nutNos")
    void deleteByUserIdAndSupplementIds(@Param("userNo") String userNo, @Param("nutNos") List<Long> nutNos);

    /**
     * 특정 사용자가 장바구니에 담아둔 특정 상품 ID 목록의 항목들을 조회
     * 이 메서드는 주문 전 유효성 검사에 사용됩니다.
     */
    @Query("SELECT ci FROM CartItem ci " +
            // ⭐️ ci.cart.user.userNo 패턴 사용
            "WHERE ci.cart.user.userNo = :userNo AND ci.supplement.nutNo IN :nutNos")
    List<CartItem> findByUserNoAndSupplementIds(@Param("userNo") String userNo, @Param("nutNos") List<Long> nutNos);

}
