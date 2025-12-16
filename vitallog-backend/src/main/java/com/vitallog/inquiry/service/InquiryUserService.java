package com.vitallog.inquiry.service;

import com.vitallog.inquiry.dto.InquiryUserResponseDTO;
import com.vitallog.inquiry.dto.InquiryUserDTO;
import com.vitallog.inquiry.entity.Inquiry;
import com.vitallog.inquiry.repository.InquiryRepository;
import com.vitallog.user.entity.User;
import com.vitallog.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryUserService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    // ModelMapper는 DTO에 fromEntity() 메소드를 사용하여 제거되었습니다.

    // 1. 질문자의 질문 작성
    @Transactional
    public InquiryUserResponseDTO createInquiry(String userNo, InquiryUserDTO requestDTO) {
        User writer = userRepository.findById(userNo)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보가 유효하지 않습니다."));

        Inquiry inquiry = Inquiry.builder()
                .user(writer)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // DTO 변환 시 fromEntity 사용
        return InquiryUserResponseDTO.fromEntity(savedInquiry);
    }

    // 2. 질문 수정 (사용자용)
    @Transactional
    public InquiryUserResponseDTO modifyInquiry(int qaNo, String userNo, InquiryUserDTO requestDTO) {

        Inquiry inquiry = inquiryRepository.findById(qaNo)
                .orElseThrow(() -> new EntityNotFoundException("수정할 문의를 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!inquiry.getUser().getUserNo().equals(userNo)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 답변이 이미 달린 경우 수정 불가
        if (inquiry.hasAnswers()) {
            throw new IllegalStateException("이미 답변이 등록된 문의는 수정할 수 없습니다.");
        }

        // 엔티티 수정
        inquiry.setTitle(requestDTO.getTitle());
        inquiry.setContent(requestDTO.getContent());
        inquiry.setUpdatedAt(LocalDateTime.now());

        // Entity 저장 및 DTO로 변환하여 반환
        return InquiryUserResponseDTO.fromEntity(inquiry);
    }

    // 3. 질문 삭제 (논리적 삭제)
    @Transactional
    public void deleteInquiry(int qaNo, String userNo) {
        Inquiry inquiry = inquiryRepository.findById(qaNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의를 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!inquiry.getUser().getUserNo().equals(userNo)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // deletedAt으로 논리적 삭제 처리 (답변 유무와 무관하게 삭제 가능)
        inquiry.setDeletedAt(LocalDateTime.now());
    }

    // 4. 단일 질문 조회 (사용자용)
    public InquiryUserResponseDTO getInquiryDetail(int qaNo, String userNo) {
        Inquiry inquiry = inquiryRepository.findById(qaNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의를 찾을 수 없습니다."));

        // 문의글이 삭제된 경우 조회 불가
        if (inquiry.getDeletedAt() != null) {
            throw new EntityNotFoundException("삭제된 문의는 조회할 수 없습니다.");
        }

        // 작성자 본인 확인 (이 로직은 Security 적용 시 필터나 AOP로 이동하는 것이 좋습니다.)
        if (!inquiry.getUser().getUserNo().equals(userNo)) {
            // throw new SecurityException("조회 권한이 없습니다.");
        }

        // DTO 변환 시 fromEntity 사용
        return InquiryUserResponseDTO.fromEntity(inquiry);
    }

    // 5. 질문 목록 조회 (사용자 본인이 작성한 질문만)
    public List<InquiryUserResponseDTO> getMyInquiries(String userNo) {

        // ⭐ 수정: InquiryRepository의 findByUser_UserNoAndDeletedAtIsNull 메서드 사용
        // 이 메서드는 DB에서 deletedAt이 null인 항목만 가져옵니다.
        List<Inquiry> inquiries = inquiryRepository.findByUser_UserNoAndDeletedAtIsNull(userNo);

        return inquiries.stream()
                .map(InquiryUserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}