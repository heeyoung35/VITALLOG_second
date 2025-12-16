package com.vitallog.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class ChangePwRequest {

    @Schema(description = "사용자 ID", example = "testuser")
    private String userId;
    @Schema(description = "기존 비밀번호", example = "password123")
    private String pwd; // 기존 비밀번호
    @Schema(description = "새 비밀번호", example = "newpassword123")
    private String newPassword;
}
