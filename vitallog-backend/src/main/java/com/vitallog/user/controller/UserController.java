package com.vitallog.user.controller;

import com.vitallog.user.dto.req.ChangePwRequest;
import com.vitallog.user.dto.req.SignoutRequest;
import com.vitallog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "사용자 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // 클라이언트에서 토큰 삭제 유도
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
    })
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePwRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 회원 탈퇴 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    })
    @DeleteMapping("/signout")
    public ResponseEntity<String> signout(@RequestBody SignoutRequest request) {
        userService.withdraw(request.getUserId());
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
