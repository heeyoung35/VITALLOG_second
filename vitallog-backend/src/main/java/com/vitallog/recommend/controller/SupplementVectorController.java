package com.vitallog.recommend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vitallog.recommend.dto.RankedResultDTO;
import com.vitallog.recommend.service.NativeRAGService;
import com.vitallog.recommend.service.SupplementVectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.vitallog.supplement.exception.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "영양제 추천 API", description = "영양제 추천 관련 API 입니다.")
@RestController
@RequestMapping("/api/recommend")
public class SupplementVectorController {
    @Autowired
    private NativeRAGService nativeRAGService;

    @Operation(summary = "사용자 로그 기반 영양제 추천", description = "사용자 로그를 기반으로 유사한 영양제를 추천합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "추천 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServerError", value = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"An unexpected error occurred.\", \"timestamp\": \"2024-01-01T12:00:00\"}")))
    })
    @PostMapping("")
    public ResponseEntity<List<RankedResultDTO>> nativeRag(
        @Parameter(description = "추천을 위한 사용자 로그(텍스트)") @RequestParam String query,
        @Parameter(description = "추천할 UserId(UUID)") @RequestParam String userId) throws Exception {
        List<RankedResultDTO> result = nativeRAGService.generateText(query, userId);
        return ResponseEntity.ok(result);
    }
}
