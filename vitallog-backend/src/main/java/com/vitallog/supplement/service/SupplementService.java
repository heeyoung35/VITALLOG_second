package com.vitallog.supplement.service;

import com.vitallog.supplement.dto.SupplementRequestDTO;
import com.vitallog.supplement.dto.SupplementResponseDTO;
import com.vitallog.supplement.entity.Supplement;
import com.vitallog.supplement.repository.SupplementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final ModelMapper modelMapper;


    // 영양제 등록
    public SupplementResponseDTO registSupplement(SupplementRequestDTO requestDTO) {

        Supplement newSupplement = Supplement.builder()
                .nutName(requestDTO.getNutName())
                .nutMthd(requestDTO.getNutMthd())
                .price(requestDTO.getPrice())
                .primaryFnclty(requestDTO.getPrimaryFnclty())
                .rawName(requestDTO.getRawName())
                .shape(requestDTO.getShape())
                .storageMthd(requestDTO.getStorageMthd())
                .warning(requestDTO.getWarning())
                .stock(requestDTO.getStock())
                .build();

        Supplement saveSupplement = supplementRepository.save(newSupplement);

        return modelMapper.map(saveSupplement, SupplementResponseDTO.class);
    }

    // 영양제 전체 조회(페이징 처리)
    public Page<SupplementResponseDTO> findSupplementList(Pageable pageable) {

        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = pageable.getPageSize();
        String sortDir = "nutNo";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir));

        Page<Supplement> supplementList = supplementRepository.findAll(pageRequest);

        return supplementList.map(supplement -> modelMapper.map(supplement, SupplementResponseDTO.class));
    }

    // 상품 검색 (영양제 코드로 검색)
    public SupplementResponseDTO findSupplementBynutNo(Long nutNo) {
        Supplement foundSupplement = supplementRepository.findById(nutNo).orElseThrow(
                () -> new IllegalArgumentException("없는 영양제 번호입니다."));

        return modelMapper.map(foundSupplement,SupplementResponseDTO.class);
    }

    // 상품 검색 (영양제 이름에 특정 단어 포함)
    public List<Supplement> findSupplementByNutName(String nutName) {
        return supplementRepository.findByNutNameContaining(nutName);
    }

    // 상품 검색 (영양제 성분에 특정 단어 포함)
    public List<Supplement> findSupplementByRawName(String rawName) {
        return supplementRepository.findByRawNameContaining(rawName);
    }

    // 상품 검색 (영양제 효능에 특정 단어 포함)
    public List<Supplement> findSupplementByPrimaryFnclty(String primaryFnclty) {
        return supplementRepository.findByPrimaryFncltyContaining(primaryFnclty);
    }

    // 상품 검색 (영양제 형태에 단어 포함)
    public List<Supplement> findSupplementByShape(String shape) {
        return supplementRepository.findByShapeContaining(shape);
    }

    // 상품 검색 (영양제 주의사항 단어 포함)
    public List<Supplement> findSupplementByWarring(String warning) {
        return supplementRepository.findByWarningContaining(warning);
    }

    // 영양제 상세 내용 수정
    @Transactional
    public SupplementResponseDTO modifySupplement(Long nutNo, SupplementRequestDTO requestDTO) {
        Supplement foundSupplement = supplementRepository.findById(nutNo)
                .orElseThrow( () -> new IllegalArgumentException("수정 할 영양제가 없습니다. 다시 입력해주세요~!"));

        foundSupplement.modify(
                requestDTO.getNutName(),
                requestDTO.getShape(),
                requestDTO.getNutMthd(),
                requestDTO.getPrimaryFnclty(),
                requestDTO.getWarning(),
                requestDTO.getStorageMthd(),
                requestDTO.getRawName(),
                requestDTO.getStock(),
                requestDTO.getPrice()
        );
        return modelMapper.map(foundSupplement, SupplementResponseDTO.class);
    }

    // 영양제 삭제 기능
    @Transactional
    public String deleteSupplement(Long nutNo) {

        Supplement supplement = supplementRepository.findById(nutNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 되는 영양제가 없습니다. 다시 입력해주세요~!"));

        String deleteName = supplement.getNutName();

        supplementRepository.delete(supplement);

        return deleteName;
    }

}
