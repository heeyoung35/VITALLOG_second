package com.vitallog.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원 탈퇴 요청")
public class SignoutRequest {
    @Schema(description = "사용자 ID", example = "testuser")
    private String userId;
    @Schema(description = "비밀번호", example = "password123")
    private String pwd;
}
