package com.vitallog.recommend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RankedResponseDTO {
    private List<RankedResultDTO> ranked_results;
}

