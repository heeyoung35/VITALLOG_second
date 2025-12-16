package com.vitallog.faq.service;


import com.vitallog.faq.dto.FaqAdminDTO;
import com.vitallog.faq.entity.Faq;
import com.vitallog.faq.repository.FaqRepository;
import com.vitallog.user.entity.User;
import com.vitallog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FaqAdminService {

    private final FaqRepository faqRepository;
    private final UserRepository userRepository;
    // ⭐⭐⭐ Builder로 변경 예정 ⭐⭐⭐
    private final ModelMapper modelMapper;

    /* 1. FaqNo로 FAQ 조회(관리자용) */
    public FaqAdminDTO findFaqForAdmin(int FaqNo) {

        Faq foundFaq = faqRepository.findById(FaqNo).orElseThrow(
                () -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));

        // DTO 반환 시 작성자 ID 수동 설정 로직 추가
        FaqAdminDTO responseDTO = modelMapper.map(foundFaq, FaqAdminDTO.class);
        responseDTO.setUserNo(foundFaq.getCreator().getUserNo()); // ⭐ 수정된 부분
        return responseDTO;
    }

    /* 2. 전체 메뉴 목록 조회(페이징 처리) */

    public Page<FaqAdminDTO> findFaqList(Pageable pageable) {

        // 페이지 번호 보정
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = pageable.getPageSize();
        String sortDir = "faqNo";

        // PageRequest 객체 생성 (페이지 번호, 사이즈, 정렬 방법)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 조회 (findAll)
        Page<Faq> faqList = faqRepository.findAll(pageRequest);

        // Page 변환 시 작성자 ID 수동 설정 로직 추가
        return faqList.map(faq -> {
            FaqAdminDTO dto = modelMapper.map(faq, FaqAdminDTO.class);
            // 지연 로딩 문제 방지를 위해 creator가 null이 아닌지 확인 후 userNo 설정
            if (faq.getCreator() != null) {
                dto.setUserNo(faq.getCreator().getUserNo()); // ⭐ 수정된 부분
            }
            return dto;
        });
    }

    /* 3. Faq 등록  */
    @Transactional
    public FaqAdminDTO registFaq(FaqAdminDTO requestDTO) {

        // 1. 작성자 User 엔티티 조회
        // 실제 운영 환경에서는 security context에서 userNo를 얻어와야 하지만,
        // 여기서는 requestDTO에서 받은 userNo로 조회합니다.
        User creator = userRepository.findById(requestDTO.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보가 유효하지 않습니다."));

        Faq newfaq = Faq.builder()
//                .userNo(requestDTO.getUserNo())
                .creator (creator)
                .category(requestDTO.getCategory())
                .question(requestDTO.getQuestion())
                .answerContent(requestDTO.getAnswerContent())
                // ⭐ 필수 초기값 설정
                .isVisible(true) // 등록 시 기본값은 공개(true)
                .createdAt(LocalDateTime.now())   // 등록 시점 시간 기록
                // updatedAt과 deletedAt은 null로 둡니다.
                .updatedAt(null)
                .deletedAt(null)
                .build(); // 빌더 패턴 완료

        Faq savedFaq = faqRepository.save(newfaq);

        // ⭐ DTO로 매핑 후, 작성자 ID를 수동으로 설정합니다. ⭐
        FaqAdminDTO responseDTO = modelMapper.map(savedFaq, FaqAdminDTO.class);
        responseDTO.setUserNo(savedFaq.getCreator().getUserNo()); // ⭐ 수정된 부분

        return responseDTO;
    }

    /* 4. Faq 수정 */
    @Transactional
    public FaqAdminDTO modifyFaq(int faqNo, FaqAdminDTO requestDTO) {

        Faq foundFaq = faqRepository.findById(faqNo)
                .orElseThrow(() -> new IllegalArgumentException("수정할 FAQ가 존재하지 않습니다."));

        // 1. 수정자 User 엔티티 조회 (수정자 기록용)
        User modifier = userRepository.findById(requestDTO.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("수정자 정보가 유효하지 않습니다."));

        // 변경할 FAQ 조회 및 수정 메서드 호출
        foundFaq.modify(
                modifier,
                requestDTO.getCategory(),
                requestDTO.getQuestion(),
                requestDTO.getAnswerContent(),
                requestDTO.getIsVisible()
        );

        // ⭐ DTO로 매핑 후, 작성자 ID를 수동으로 설정합니다. ⭐
        FaqAdminDTO responseDTO = modelMapper.map(foundFaq, FaqAdminDTO.class);
        responseDTO.setUserNo(foundFaq.getCreator().getUserNo()); // ⭐ 수정된 부분

        return responseDTO;
    }

    /* 5. Faq 논리적 삭제 */
    @Transactional
    public void deleteFaq(int faqNo) {
        Faq foundFaq = faqRepository.findById(faqNo)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 FAQ가 존재하지 않습니다."));

        // Entity의 논리적 삭제 메서드 호출
        foundFaq.delete();
    }
}