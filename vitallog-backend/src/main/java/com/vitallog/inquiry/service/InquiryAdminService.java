package com.vitallog.inquiry.service;

import com.vitallog.inquiry.dto.InquiryAdminDTO;
import com.vitallog.inquiry.dto.InquiryResponseDTO;
import com.vitallog.inquiry.entity.Inquiry;
import com.vitallog.inquiry.entity.InquiryReply;
import com.vitallog.inquiry.repository.InquiryRepository;
import com.vitallog.inquiry.repository.InquiryReplyRepository; // ⭐ InquiryReplyRepository import
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
// import org.modelmapper.ModelMapper; // 사용하지 않는다면 제거하는 것이 좋습니다.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryAdminService {

    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository; // ⭐ 1. InquiryReplyRepository 주입
    // private final ModelMapper modelMapper; // ModelMapper는 현재 사용되지 않습니다.

    /* 2. qaNo으로 Inquiry 조회 (관리자용) */
    public InquiryResponseDTO findInquiryForAdmin(int qaNo) {
        Inquiry inquiry = inquiryRepository.findById(qaNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의를 찾을 수 없습니다."));

        return InquiryResponseDTO.fromEntity(inquiry);
    }

    // 4 & 5. 관리자의 답변 작성 및 추가 답변 작성 (답변 삭제는 불가능)
    @Transactional
    public InquiryResponseDTO addReply(int qaNo, String adminNo, InquiryAdminDTO requestDTO) {
        Inquiry inquiry = inquiryRepository.findById(qaNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의를 찾을 수 없습니다."));

        // 답변 Entity 생성 (규칙 4, 5 반영: 기존 답변 유지 및 새 답변 추가)
        InquiryReply reply = InquiryReply.builder()
                .inquiry(inquiry)
                .adminNo(adminNo) // Controller에서 받은 adminNo 사용
                .answerContent(requestDTO.getAnswerContent())
                .createdAt(LocalDateTime.now())
                .build();

        // ⭐ 2. InquiryReplyRepository를 사용하여 명시적으로 저장 (ID 할당 보장)
        InquiryReply savedReply = inquiryReplyRepository.save(reply);

        // 3. Inquiry Entity의 replies 리스트에 새 답변 추가하여 동기화
        //    (저장된 객체(savedReply)를 추가하는 것이 안전합니다.)
        inquiry.getReplies().add(savedReply);

        return InquiryResponseDTO.fromEntity(inquiry);
    }

    // 8. 전체 질문 목록 조회 (관리자용)
    public List<InquiryResponseDTO> getAllInquiries() {
        List<Inquiry> inquiries = inquiryRepository.findAll();

        return inquiries.stream()
                .map(InquiryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}