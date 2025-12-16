package com.vitallog.supplement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SupplementDTO {

    private Long nutNo;
    private String nutMthd;
    private String nutName;
    private int price;
    private String primaryFnclty;
    private String rawName;
    private String shape;
    private String storageMthd;
    private String warning;
    private int stock;
}
