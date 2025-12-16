package com.vitallog.faq.entity;

import com.vitallog.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "faq")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_no")
    private int faqNo;
//    @Column(name = "user_no")
//    private String userNo;

    // N:1 관계 설정: FAQ 작성자 (외래 키를 가짐, 연관 관계의 주인)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_no", referencedColumnName = "user_no", nullable = false)
    private User creator;

    @Column(name = "category", nullable = false)
    private String category;
    @Column(name = "question", nullable = false)
    private String question;
    @Column(name = "answer_content", nullable = false)
    private String answerContent;
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void modify(User modifier, String category, String question, String answerContent, Boolean isVisible) { // 수정자 정보를 User 객체로 받도록 변경
        // this.userNo = userNo; <--- String 대신 User 객체를 사용하여 수정자 정보 기록 (creator 필드가 작성자로 사용되므로, 수정자 필드를 따로 두는 것을 고려해야 합니다. 여기서는 creator를 작성자로 유지합니다.)
        this.category = category;
        this.question = question;
        this.answerContent = answerContent;
        this.isVisible = isVisible;
        this.updatedAt = LocalDateTime.now(); // 수정 시각 갱신
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
