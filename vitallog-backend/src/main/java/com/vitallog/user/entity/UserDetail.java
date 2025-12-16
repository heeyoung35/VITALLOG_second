package com.vitallog.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;


@Entity
@Table(name = "detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail implements Persistable<String> {

    @Id
    @Column(name = "user_no", nullable = false)
    private String userNo;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "height", nullable = false)
    private Double height;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "is_pregnant", nullable = false)
    private boolean isPregnant;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.userNo;
    }
    // [수정 3] Persistable 인터페이스 구현 (새 엔티티 여부 반환)
    @Override
    public boolean isNew() {
        return this.isNew;
    }
    // [수정 4] DB 조회 후(PostLoad) 또는 저장 전(PrePersist)에는 기존 데이터로 취급
    @PostLoad
    @PrePersist
    public void markNotNew() {
        this.isNew = false;
    }
}
