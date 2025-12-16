package com.vitallog.user.entity;

import com.vitallog.common.UserRole;
import com.vitallog.faq.entity.Faq;
import com.vitallog.inquiry.entity.Inquiry;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_no", nullable = false)
    private String userNo;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "id", nullable = false, unique = true)
    private String userId;

    @Column(name = "pwd", nullable = false)
    private String pwd;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void generateInfo() {
        // 1. userNo 생성 로직
        if (this.userNo == null) {
            this.userNo = UUID.randomUUID().toString();
        }

        // 2. createdAt 생성 로직 (보통 같이 처리합니다)
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }


    public List<String> getRoleList() {
        if (this.role != null) {
            // Enum 권한을 문자열 리스트로 변환하여 반환
            return Arrays.asList(this.role.toString());
        }
        return new ArrayList<>();
    }

    // 1. Inquiry와의 1:N 관계: 이 사용자가 작성한 문의 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Inquiry> inquiries = new ArrayList<>();

    // 2. Faq와의 1:N 관계: 이 사용자가 작성한 FAQ 목록
    // mappedBy의 "creator"는 Faq 엔티티의 필드 이름과 일치해야 합니다.
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Faq> createdFaqs = new ArrayList<>();
}

