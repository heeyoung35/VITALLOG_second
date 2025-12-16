package com.vitallog.recommend.dto;

import lombok.Data;

@Data
public class RankedResultDTO {
    private int rank;
    private String name;
    private int score;
    private String percentage;
    private String reason;
}

