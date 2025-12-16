package com.vitallog.inquiry.controller;

import com.vitallog.inquiry.dto.InquiryAdminDTO;
import com.vitallog.inquiry.dto.InquiryUserDTO;
import com.vitallog.inquiry.dto.InquiryResponseDTO;
import com.vitallog.inquiry.dto.InquiryUserResponseDTO;
import com.vitallog.inquiry.service.InquiryAdminService;
import com.vitallog.inquiry.service.InquiryUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "1:1 문의 API", description = "문의 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryUserService userService;
    private final InquiryAdminService adminService;

    // ⭐ 임시 사용자/관리자 ID (실제는 Security Context에서 가져옴)
    private static final String TEMP_USER_NO = "123e4567-e89b-12d3-a456-426614174001";
    private static final String TEMP_ADMIN_NO = "admin-uuid-001"; // 예시

    @Operation(summary = "문의 작성", description = "사용자가 새로운 문의를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (사용자를 찾을 수 없음)")
    })
    @PostMapping("/inquiry")
    public ResponseEntity<InquiryUserResponseDTO> createInquiry(@RequestBody InquiryUserDTO requestDTO) {
        try {
            InquiryUserResponseDTO newInquiry = userService.createInquiry(TEMP_USER_NO, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newInquiry);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "문의 수정", description = "사용자가 작성한 문의를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @PutMapping("/inquiry/{qaNo}")
    public ResponseEntity<InquiryUserResponseDTO> modifyInquiry(@PathVariable int qaNo,
                                                                @RequestBody InquiryUserDTO requestDTO) {
        try {
            InquiryUserResponseDTO updatedInquiry = userService.modifyInquiry(qaNo, TEMP_USER_NO, requestDTO);
            return ResponseEntity.ok(updatedInquiry);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "문의 상세 조회 (사용자용)", description = "사용자가 작성한 문의의 상세 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @GetMapping("/inquiry/{qaNo}")
    public ResponseEntity<InquiryUserResponseDTO> getInquiryDetail(@PathVariable int qaNo) {
        try {
            InquiryUserResponseDTO inquiry = userService.getInquiryDetail(qaNo, TEMP_USER_NO);
            return ResponseEntity.ok(inquiry);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "내 문의 목록 조회", description = "사용자가 작성한 모든 문의 목록을 조회합니다.")
    @GetMapping("/inquiries/my")
    public ResponseEntity<List<InquiryUserResponseDTO>> getMyInquiries() {
        List<InquiryUserResponseDTO> inquiries = userService.getMyInquiries(TEMP_USER_NO);
        return ResponseEntity.ok(inquiries);
    }

    @Operation(summary = "문의 삭제", description = "사용자가 작성한 문의를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @DeleteMapping("/inquiry/{qaNo}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable int qaNo) {
        try {
            userService.deleteInquiry(qaNo, TEMP_USER_NO);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "문의 답변 작성/추가 (관리자용)", description = "관리자가 문의에 대한 답변을 작성하거나 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 작성 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @PostMapping("/admin/inquiry/{qaNo}/reply")
    public ResponseEntity<InquiryResponseDTO> addReply(@PathVariable int qaNo,
                                                       @RequestBody InquiryAdminDTO requestDTO) {
        try {
            InquiryResponseDTO updatedInquiry = adminService.addReply(qaNo, TEMP_ADMIN_NO, requestDTO);
            return ResponseEntity.ok(updatedInquiry);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "전체 문의 목록 조회 (관리자용)", description = "관리자가 모든 사용자 문의 목록을 조회합니다.")
    @GetMapping("/admin/inquiries")
    public ResponseEntity<List<InquiryResponseDTO>> getAllInquiries() {
        List<InquiryResponseDTO> inquiries = adminService.getAllInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @Operation(summary = "문의 상세 조회 (관리자용)", description = "관리자가 특정 문의의 상세 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @GetMapping("/admin/inquiry/{qaNo}")
    public ResponseEntity<InquiryResponseDTO> getInquiryForAdmin(@PathVariable int qaNo) {
        try {
            InquiryResponseDTO inquiry = adminService.findInquiryForAdmin(qaNo);
            return ResponseEntity.ok(inquiry);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
