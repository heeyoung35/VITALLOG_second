package com.vitallog.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {

    @Schema(description = "사용자 아이디", example = "vital_user")
    private String userId;

    @Schema(description = "비밀번호", example = "password123!")
    private String pwd;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "역할 (USER/ADMIN)", example = "USER")
    private String role;

    @Schema(description = "성별 (M/F)", example = "M")
    private String gender;

    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "키 (cm)", example = "175.5")
    private double height;

    @Schema(description = "몸무게 (kg)", example = "70.0")
    private double weight;

    @Schema(description = "임신 여부", example = "false")
    private boolean pregnant;

}
