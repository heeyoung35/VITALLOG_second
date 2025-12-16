package com.vitallog.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "아이디 찾기 요청")
public class FindIdRequest {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "이메일", example = "test@test.com")
    private String email;
}
