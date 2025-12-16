package com.vitallog.inquiry.repository;

import com.vitallog.inquiry.entity.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Integer> {

    /**
     * 특정 질문(qaNo)에 연결된 모든 답변 목록을 조회합니다.
     * Inquiry 엔티티에서 @OneToMany 관계를 통해 자동으로 로딩되므로,
     * Service에서 직접 호출할 일은 적지만 필요하다면 정의할 수 있습니다.
     */
    List<InquiryReply> findByInquiry_QaNo(int qaNo);
}