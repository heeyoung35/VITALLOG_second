package com.vitallog.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO") // [추가]
public class LoginResponse {

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "로그인한 사용자 정보")
    private UserResponse user;
}
