package com.vitallog.supplement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank; // (O)



@Getter
@Setter
@ToString
@NoArgsConstructor
public class SupplementRequestDTO {

    @NotBlank(message = "복용 방법은 필수 입력 갑입니다.")
    private String nutMthd;
    @NotBlank(message = "영양제 이름은 필수 입력 값입니다.")
    private String nutName;
    @NotNull(message = "가격은 필수 입력 값입니다.")
    private int price;
    @NotBlank(message = "효능은 필수 입력 값입니다.")
    private String primaryFnclty;
    @NotBlank(message = "성분은 필수 입력 값입니다.")
    private String rawName;
    @NotBlank(message = "형태는 필수 입력 값입니다.")
    private String shape;
    @NotBlank(message = "보관 방법은 필수 입력 값입니다.")
    private String storageMthd;
    @NotBlank(message = "주의사항은 필수 입력 값입니다.")
    private String warning;
    @NotNull(message = "재고는 필수 입력 값입니다.")
    private int stock;
}
