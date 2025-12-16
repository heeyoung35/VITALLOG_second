package com.vitallog.faq.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FaqAdminDTO {

    private int faqNo; // ⭐ 추가된 부분

    @Column(name = "user_no")
    private String userNo;
    private String category;
    private String question;
    private String answerContent;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
