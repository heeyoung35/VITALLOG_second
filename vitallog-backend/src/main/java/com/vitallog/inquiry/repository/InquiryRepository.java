package com.vitallog.inquiry.repository;

import com.vitallog.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {

    /**
     * 특정 사용자(userNo)가 작성한, 삭제되지 않은 모든 질문 목록을 조회합니다.
     * 논리적으로 삭제되지 않은 문의만 조회하려면 WHERE deletedAt IS NULL 조건을 추가합니다.
     */
    // ⭐ 이 메서드 이름이 정확해야 합니다. (AndDeletedAtIsNull)
    List<Inquiry> findByUser_UserNoAndDeletedAtIsNull(String userNo);
}