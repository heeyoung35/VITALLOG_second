package com.vitallog.faq.repository;

import com.vitallog.faq.entity.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

    // 1. 특정 FAQ 조회 시 deletedAt이 null인 경우만 조회 (회원용)
    Optional<Faq> findByFaqNoAndDeletedAtIsNull(int faqNo); // ⭐ 추가

    // 2. 카테고리별 FAQ 목록 조회 시 deletedAt이 null인 경우만 조회 (회원용)
    Page<Faq> findByCategoryAndDeletedAtIsNull(String category, Pageable pageable); // ⭐ findByCategory 수정

    // 3. 전체 FAQ 목록 조회 시 deletedAt이 null인 경우만 조회 (회원용)
    Page<Faq> findAllByDeletedAtIsNull(Pageable pageable); // ⭐ 추가
}
