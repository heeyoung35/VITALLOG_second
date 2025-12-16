package com.vitallog.faq.controller;

import com.vitallog.faq.dto.FaqAdminDTO;
import com.vitallog.faq.dto.FaqUserDTO;
import com.vitallog.faq.service.FaqAdminService;
import com.vitallog.faq.service.FaqUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FAQ API", description = "FAQ 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FaqController {

    private final FaqUserService faqUserService;
    private final FaqAdminService faqAdminService;

    @Operation(summary = "FAQ 상세 조회(회원용)", description = "FAQ 번호로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "FAQ를 찾을 수 없음")
    })
    @GetMapping("/faq/{faqNo}")
    public ResponseEntity<FaqUserDTO> findFaqForUser(@PathVariable int faqNo) {
        FaqUserDTO resultFaq = faqUserService.findFaqForUser(faqNo);

        return ResponseEntity.ok(resultFaq);
    }

    @Operation(summary = "FAQ 상세 조회(관리자용)", description = "FAQ 번호로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "FAQ를 찾을 수 없음")
    })
    @GetMapping("/admin/faq/{faqNo}")
    public ResponseEntity<FaqAdminDTO> findFaqForAdmin(@PathVariable int faqNo) {
        FaqAdminDTO resultFaq = faqAdminService.findFaqForAdmin(faqNo);

        return ResponseEntity.ok(resultFaq);
    }

    @Operation(summary = "FAQ 목록 조회(회원용)", description = "페이징 처리된 FAQ 목록을 조회합니다.")
    @GetMapping("/faqs")
    public ResponseEntity<Page<FaqUserDTO>> findFaqListForUser(@PageableDefault(size = 10, sort = "faqNo") Pageable pageable) {
        Page<FaqUserDTO> faqList = faqUserService.findFaqList(pageable);

        return ResponseEntity.ok(faqList);
    }

    @Operation(summary = "FAQ 목록 조회(관리자용)", description = "페이징 처리된 FAQ 목록을 조회합니다.")
    @GetMapping("/admin/faqs")
    public ResponseEntity<Page<FaqAdminDTO>> findFaqListForAdmin(@PageableDefault(size = 10, sort = "faqNo") Pageable pageable) {
        Page<FaqAdminDTO> faqList = faqAdminService.findFaqList(pageable);

        return ResponseEntity.ok(faqList);
    }

    @Operation(summary = "카테고리별 FAQ 목록 조회(회원용)", description = "특정 카테고리의 FAQ 목록을 조회합니다.")
    @GetMapping("/faq/search")
    public ResponseEntity<Page<FaqUserDTO>> findByFaqCategory(@RequestParam String category, Pageable pageable) {
        Page<FaqUserDTO> faqList = faqUserService.findByCategory(category, pageable);

        return ResponseEntity.ok(faqList);
    }

    @Operation(summary = "FAQ 등록", description = "새로운 FAQ를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/admin/faq")
    public ResponseEntity<FaqAdminDTO> registFaq(@RequestBody FaqAdminDTO requestDTO) {
        FaqAdminDTO newFaq = faqAdminService.registFaq(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newFaq);
    }

    @Operation(summary = "FAQ 수정", description = "기존 FAQ를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "FAQ를 찾을 수 없음")
    })
    @PutMapping("admin/faq/{faqNo}")
    public ResponseEntity<FaqAdminDTO> modifyFaq(@PathVariable int faqNo, @RequestBody FaqAdminDTO requestDTO) {

        FaqAdminDTO updatedFaq = faqAdminService.modifyFaq(faqNo, requestDTO);

        return ResponseEntity.ok(updatedFaq);
    }

    @Operation(summary = "FAQ 삭제", description = "FAQ를 논리적으로 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "FAQ를 찾을 수 없음")
    })
    @DeleteMapping("/admin/faq/{faqNo}")
    public ResponseEntity<Void> deleteFaq(@PathVariable int faqNo) {
        faqAdminService.deleteFaq(faqNo);

        return ResponseEntity.noContent().build();
    }
}
