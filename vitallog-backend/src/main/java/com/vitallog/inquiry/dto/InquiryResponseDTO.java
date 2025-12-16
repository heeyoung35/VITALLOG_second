package com.vitallog.inquiry.dto;

import com.vitallog.inquiry.entity.Inquiry;
import com.vitallog.inquiry.entity.InquiryReply;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryResponseDTO {

    // 1. Inquiry 기본 정보 (InquiryUserDTO 필드 포함)
    private int qaNo;
    private String userNo; // 질문자 UUID
    private String title;
    private String content;

    // 2. 시간 및 상태 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;   // deletedAt != null
    private boolean isAnswered;  // hasAnswers()

    // 3. 답변 목록 (1:N 관계)
    private List<ReplyDTO> replies;

    /**
     * Inquiry Entity -> InquiryResponseDTO 변환 메서드
     */
    public static InquiryResponseDTO fromEntity(Inquiry entity) {
        // 1:N 관계인 답변 목록을 ReplyDTO로 변환
        List<ReplyDTO> replyList = entity.getReplies().stream()
                .map(ReplyDTO::fromEntity)
                .collect(Collectors.toList());

        return InquiryResponseDTO.builder()
                .qaNo(entity.getQaNo())
                .userNo(entity.getUserNo())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getDeletedAt() != null)
                .isAnswered(entity.hasAnswers()) // Inquiry Entity의 헬퍼 메서드 사용
                .replies(replyList)
                .build();
    }

    // ==========================================================
    // 답변 정보를 담는 내부 DTO: ReplyDTO
    // ==========================================================

    // InquiryReply Entity의 정보를 담습니다.
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReplyDTO {

        private int replyNo;
        private String adminNo; // 답변자 UUID
        private String content;
        private LocalDateTime createdAt;

        /**
         * InquiryReply Entity -> ReplyDTO 변환 메서드
         */
        public static ReplyDTO fromEntity(InquiryReply entity) {
            return ReplyDTO.builder()
                    .replyNo(entity.getReplyNo())
                    .adminNo(entity.getAdminNo())
                    .content(entity.getAnswerContent())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}