package com.vitallog.user.dto.req;

import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "사용자 아이디", example = "admin_real")
    private String userId;
    @Schema(description = "비밀번호", example = "admin1234")
    private String pwd;
}
