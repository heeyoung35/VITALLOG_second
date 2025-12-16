package com.vitallog.user.controller;

import com.vitallog.user.dto.req.FindIdRequest;
import com.vitallog.user.dto.req.FindPwRequest;
import com.vitallog.user.dto.req.LoginRequest;
import com.vitallog.user.dto.req.SignupRequest;
import com.vitallog.user.dto.res.LoginResponse;
import com.vitallog.user.entity.User;
import com.vitallog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "회원 인증 API", description = "회원가입, 로그인 및 인증 관련 API")
public class AuthController {

    private final UserService userService;

    // api/user/signup
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        userService.signup(signupRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // api/user/login
    @PostMapping("/login/web/{userId}/{pwd}")
    public ResponseEntity<LoginResponse> login(@PathVariable String userId, @PathVariable String pwd) {
        LoginRequest loginRequest = new LoginRequest(userId, pwd);
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    // api/user/login
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 이용하여 로그인하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    // api/user/check-id
    @GetMapping("/check-id")
    @Operation(summary = "중복아이디 체크", description = "중복된 아이디인지 체크하고 확인합니다. ")
    public ResponseEntity<Boolean> checkId(@RequestParam String userId) {
        return ResponseEntity.ok(userService.checkIdDuplicate(userId));
    }

    // api/user/check-email
    @GetMapping("/check-email")
    @Operation(summary = "중복이메일 체크", description = "중복된 이메일인지 체크하고 확인입니다.")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.checkEmailDuplicate(email));
    }

    // api/user/find-id
    @PostMapping("/find-id")
    @Operation(summary = "아이디 찾기", description = "회원의 이름과 이메일을 입력받아 일치하는 회원 아이디를 반환합니다.")
    public ResponseEntity<String> findId(@RequestBody FindIdRequest request) {
        String foundId = userService.findId(request);
        return ResponseEntity.ok("회원님의 아이디는 [" + foundId + "] 입니다.");
    }

    // api/user/reset-password
    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 초기화", description = "아이디와 이메일을 입력받아 일치 시 임시 비밀번호를 전송합니다.")
    public ResponseEntity<String> resetPassword(@RequestBody FindPwRequest request) {
        userService.findPassword(request);
        return ResponseEntity.ok("이메일로 임시 비밀번호가 발송되었습니다.");
    }

    // api/user/change-password
    @PostMapping("/change-password")
    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호 확인 후 새로운 비밀번호로 변경합니다.")
    public ResponseEntity<String> changePassword(@RequestBody com.vitallog.user.dto.req.ChangePwRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // api/user/withdraw
    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "계정과 해당 정보를 모두 삭제합니다.")
    public ResponseEntity<String> withdraw(@RequestParam String userId) {
        userService.withdraw(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    // api/user/reissue (토큰 재발급 - 임시 구현)
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue() {
        // Refresh Token 로직이 필요하지만 우선 200 OK 반환
        return ResponseEntity.ok("토큰이 재발급되었습니다.");
    }
}
