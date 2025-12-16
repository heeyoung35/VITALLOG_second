package com.vitallog.supplement.repository;

import com.vitallog.supplement.entity.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Long> {

// mysql 5.7 버전부터 지원하는 비관적 락
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT s FROM Supplement s WHERE s.nutNo = :nutNo")
//    Optional<Supplement> findByNutNoWithLock(Long nutNo);

    /* 네이티브 쿼리 락 */
    @Query(
            value = "SELECT * FROM supplement WHERE nut_no = :nutNo FOR UPDATE",
            nativeQuery = true
    )
    Optional<Supplement> findByNutNoWithLock(@Param("nutNo") Long nutNo);

    // 영양제 이름
    List<Supplement> findByNutNameContaining(String nutName);

    // 영양제 성분
    List<Supplement> findByRawNameContaining(String rawName);

    // 영양제 효능
    List<Supplement> findByPrimaryFncltyContaining(String primaryFnclty);

    // 영양제 형태
    List<Supplement> findByShapeContaining(String shape);

    // 영양제 주의사항
    List<Supplement> findByWarningContaining(String warning);


}
