package com.vitallog.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.vitallog.config.GeminiConfig;
import com.vitallog.recommend.dto.RankedResponseDTO;
import com.vitallog.recommend.dto.RankedResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NativeRAG {
    private final GeminiConfig geminiConfig;
    private final SearchVector searchVector;
    private final Gson gson = new Gson();
    @Autowired
    public NativeRAG(GeminiConfig geminiConfig,  SearchVector searchVector) {
        this.geminiConfig = geminiConfig;
        this.searchVector = searchVector;
    }

    public List<RankedResultDTO> generateText(String inputText) throws JsonProcessingException {
        // 1. 영양제 검색
        List<Object> nutDB = searchVector.search(inputText);

        String nutDBString = nutDB.stream().map(Object::toString).collect(Collectors.joining(","));

        // 2. 회원 검색(임시)
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> userData = Map.of(
            "user_no", 101,
            "gender", "female",     // male/female
            "age", 29,
            "height", 165,          // cm
            "weight", 55,           // kg
            "is_pregnant", true   // Boolean
        );

        String userDataString = mapper.writeValueAsString(userData);

        // 3. prompt 작성
        String prompt = """
            You are a Nutritional Supplement Re-ranking Agent.
            
            Your goal is to take the search results of supplements and re-rank them based on user's personal information, user requirements.
            
            You must strictly follow the scoring rules below and output the final ranking in JSON format.
            
            -----------------------------
            [Input]
            1. User Profiles:
            """ + userDataString + """
            
            2. Search Results:
            """ + nutDBString + """
            
            3. User requirements:
            """ + inputText + """
            [RE-RANKING RULES]
            
            - Evaluate each supplement strictly according to how well it fits the user's profile and requirements.
            - Consider goal match, health condition fit, safety risks, and ingredient effectiveness.
            - Penalize supplements that contain forbidden ingredients or have potential medication interactions.
            - Prioritize supplements that strongly align with the user's primary health goals.
            - Explain the reason for each score in detail, and represent the contribution of each scoring category as a percentage of the total score.
            
            -----------------------------
            [OUTPUT FORMAT]
            
            {
              "ranked_results": [
                {
                  "rank": 1,
                  "name": "솔빛Q",
                  "score": 95,
                  "percentage": "95%",\s
                  "reason": "사용자의 요구사항인 비타민 E와 매우 잘 맞고, 나이/성별에 적합하며 안전합니다."
                },
                {
                  "rank": 2,
                  "name": "토코한국보건영",
                  "score": 87,
                  "percentage": "87%",
                  "reason": "비타민 E 효능이 목표와 잘 맞지만, 일부 성분이 알레르기 위험이 있어 점수가 조금 낮습니다."
                },
                {
                  "rank": 3,
                  "name": "불로송(不.老.松)",
                  "score": 70,
                  "percentage": "70%",
                  "reason": "목표 적합도는 높지만 가격이 높고 일부 첨가물이 사용자에게 덜 적합하여 점수가 낮습니다."
                }
              ]
            }
            
            -----------------------------
            [GUIDELINES]
            - Scores must be calculated strictly according to the rules above.
            - If two supplements have the same score, prioritize the one with higher goal alignment.
            - Do not invent ingredients or health data not provided in the input.
            - Clearly explain any penalties applied due to safety risks or contraindications.
            - The 'ranked_results' array should contain a maximum of 3 supplements.
            
            Your task: Using the user profile and search results, calculate the scores, re-rank the supplements, and return the final results.
            """;


        // 4. 모델 응답 생성
        Client client = geminiConfig.GeminiModel();

        if (client == null) {
            throw new RuntimeException("Gemini Client 초기화 실패");
        }
        String modelName = geminiConfig.getModel();
        if (modelName == null || modelName.isBlank()) {
            throw new RuntimeException("Gemini 모델명이 비어 있음");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new RuntimeException("Prompt가 비어 있음");
        }

        GenerateContentResponse response;
        try {
            response = client.models.generateContent(
                modelName,
                prompt,
                null
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage());
        }

        if (response == null || response.text() == null || response.text().isBlank()) {
            throw new RuntimeException("Gemini가 유효한 응답을 반환하지 않음");
        }

        String rawOutput = response.text();

        // JSON만 추출
        int firstBrace = rawOutput.indexOf('{');
        int lastBrace = rawOutput.lastIndexOf('}');

        if (firstBrace == -1 || lastBrace == -1) {
            throw new RuntimeException("JSON 결과를 찾을 수 없음");
        }

        String jsonOutput = rawOutput.substring(firstBrace, lastBrace + 1);

        // Jackson으로 DTO 변환
        RankedResponseDTO rankedResponse = mapper.readValue(jsonOutput, RankedResponseDTO.class);

        // 리스트만 반환
        return rankedResponse.getRanked_results();
    }
}
