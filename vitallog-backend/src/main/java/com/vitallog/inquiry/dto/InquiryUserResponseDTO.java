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
public class InquiryUserResponseDTO {

    // 1. Inquiry 기본 정보
    private int qaNo;
    private String userNo; // 질문자 UUID
    private String title;
    private String content;

    // 2. 시간 및 상태 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // private boolean isDeleted; // ⭐ 삭제: 필드 제거
    private boolean isAnswered;

    // 3. 답변 목록 (1:N 관계)
    private List<ReplyDTO> replies;

    /**
     * Inquiry Entity -> InquiryUserResponseDTO 변환 메서드
     */
    public static InquiryUserResponseDTO fromEntity(Inquiry entity) {
        // 1:N 관계인 답변 목록을 ReplyDTO로 변환
        List<ReplyDTO> replyList = entity.getReplies().stream()
                // 문의글 자체가 삭제된 건 Repository에서 이미 걸렀으므로,
                // 이 필터는 이제 기능적으로는 불필요하지만 안전을 위해 유지해도 무방합니다.
                .map(ReplyDTO::fromEntity)
                .collect(Collectors.toList());

        String userNo = entity.getUser() != null ? entity.getUser().getUserNo() : null;

        return InquiryUserResponseDTO.builder()
                .qaNo(entity.getQaNo())
                .userNo(userNo)
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                // .isDeleted(entity.getDeletedAt() != null) // ⭐ 삭제: 변환 로직 제거
                .isAnswered(entity.hasAnswers())
                .replies(replyList)
                .build();
    }

    // ==========================================================
    // 답변 정보를 담는 내부 DTO: ReplyDTO
    // ==========================================================
    // (내부 ReplyDTO 코드는 변경 없음)
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReplyDTO {
        private int replyNo;
        private String content;
        private LocalDateTime createdAt;

        public static ReplyDTO fromEntity(InquiryReply entity) {
            return ReplyDTO.builder()
                    .replyNo(entity.getReplyNo())
                    .content(entity.getAnswerContent())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}