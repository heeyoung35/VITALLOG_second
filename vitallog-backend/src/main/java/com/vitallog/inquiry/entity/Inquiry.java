package com.vitallog.inquiry.entity;

import com.vitallog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inquiry")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int qaNo;
//    private String userNo;

    // N:1 관계 설정: 문의 작성자 (외래 키를 가짐, 연관 관계의 주인)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", referencedColumnName = "user_no", nullable = false)
    private User user;

    @Column(name = "title")
    private String title;
    @Lob
    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 1:N 관계 - 하나의 질문에 여러 답변 가능 (규칙 5번)
    @OneToMany(mappedBy = "inquiry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<InquiryReply> replies = new ArrayList<>();

    // 답변이 하나라도 있으면 true 반환
    public boolean hasAnswers() {
        return this.replies != null && !this.replies.isEmpty();
    }

    // ⭐⭐ 추가 필요: Service 및 DTO 호환성을 위한 커스텀 Getter
    public String getUserNo() {
        // user 객체가 null이 아닐 경우 userNo를 반환합니다.
        return this.user != null ? this.user.getUserNo() : null;
    }
}