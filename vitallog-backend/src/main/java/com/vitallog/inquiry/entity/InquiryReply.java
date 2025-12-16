package com.vitallog.inquiry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry_reply")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InquiryReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_no")
    private int replyNo;

    // 어떤 질문에 대한 답변인지 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_no", nullable = false)
    private Inquiry inquiry;

    // FK: 답변자 (관리자 String UUID)
    private String adminNo;
    @Lob
    @Column(name = "answer_content")
    private String answerContent;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
