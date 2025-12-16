package com.vitallog.user.repository;

import com.vitallog.user.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDetailRepository extends JpaRepository<UserDetail, String> {

    // 사용자 번호로 상세 정보 조회
    Optional<UserDetail> findByUserNo(String userNo);

    void deleteByUserNo(String userNo);

}
