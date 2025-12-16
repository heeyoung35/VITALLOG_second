package com.vitallog.faq.service;

import com.vitallog.faq.dto.FaqUserDTO;
import com.vitallog.faq.entity.Faq;
import com.vitallog.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaqUserService {

    private final FaqRepository faqRepository;
    private final ModelMapper modelMapper;

    /* 1. FaqNo으로 FAQ 조회(회원용) */
    public FaqUserDTO findFaqForUser(int FaqNo) {

        // ⭐ 수정: findByFaqNoAndDeletedAtIsNull 사용
        Faq foundFaq = faqRepository.findByFaqNoAndDeletedAtIsNull(FaqNo).orElseThrow(
                () -> new IllegalArgumentException("FAQ가 존재하지 않거나 삭제되었습니다."));

        return modelMapper.map(foundFaq, FaqUserDTO.class);
    }

    /* 2. 전체 메뉴 목록 조회(페이징 처리) */
    public Page<FaqUserDTO> findFaqList(Pageable pageable) {

        // 페이지 번호 보정
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = pageable.getPageSize();
        String sortDir = "faqNo";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // ⭐ 수정: findAllByDeletedAtIsNull 사용
        Page<Faq> faqList =faqRepository.findAllByDeletedAtIsNull(pageRequest);

        return faqList.map(faq -> modelMapper.map(faq, FaqUserDTO.class));
    }

    /* 3. 특정 카테고리로 Faq 목록 조회(페이징 처리) */
    public Page<FaqUserDTO> findByCategory(String category, Pageable pageable) {

        // ⭐ 수정: findByCategoryAndDeletedAtIsNull 사용
        Page<Faq> faqList = faqRepository.findByCategoryAndDeletedAtIsNull(category, pageable);

        return faqList.map(faq -> modelMapper.map(faq, FaqUserDTO.class));
    }
}