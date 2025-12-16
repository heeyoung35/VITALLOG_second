package com.vitallog.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비밀번호 찾기 요청")
public class FindPwRequest {
    @Schema(description = "사용자 아이디", example = "testuser")
    private String userId;
    @Schema(description = "이메일", example = "test@test.com")
    private String email;
}
