package com.vitallog.recommend.service;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vitallog.config.GeminiConfig;
import com.vitallog.config.VectorizationModelConfig;
import com.vitallog.recommend.dto.RankedResponseDTO;
import com.vitallog.recommend.dto.RankedResultDTO;
import com.vitallog.recommend.repository.SupplementVectorRepository;
import com.vitallog.user.entity.UserDetail;

import com.vitallog.user.repository.UserDetailRepository;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SpringBootApplication
public class NativeRAGService {
    private final GeminiConfig geminiConfig;
    private final VectorizationModelConfig vectorModel;
    private final SupplementVectorRepository supplementVectorRepository;
    private final UserDetailRepository userDetailRepository;

    private final Gson gson = new Gson();

    public List<RankedResultDTO> generateText(String inputText, String userId) throws JsonProcessingException {
        //0. 사용자 입력 임베딩
        Response<Embedding> vectorizationText = vectorModel.embeddingModel().embed(inputText);
        Embedding embedding = vectorizationText.content();
        float[] vector = embedding.vector();

        // 1. 영양제 검색
        List<Object> nutDB = supplementVectorRepository.search(vector);
        String nutDBString = nutDB.stream().map(Object::toString).collect(Collectors.joining(","));

        // 2. 회원 검색(임시) - userDetailString을 직접 사용
         Optional<UserDetail> userDetail = userDetailRepository.findByUserNo(userId);

         String userDetailString = new  ObjectMapper().writeValueAsString(userDetail.get());

        //System.out.println(userDetailString);

        // 3. prompt 작성
        String prompt = """
            You are a Nutritional Supplement Re-ranking Agent.
            
            Your goal is to take the search results of supplements and re-rank them based on user's personal information, user requirements.
            
            You must strictly follow the scoring rules below and output the final ranking in JSON format.
            
            -----------------------------
            [Input]
            1. User Profiles:
            """ + userDetailString + """
            
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
                   "name": "회화춘",
                   "score": 70,
                   "percentage": "70%",
                   "reason": "임산부에게 주의가 필요한 성분(에스트로겐 호르몬 민감성) 경고가 있으나, 비타민 C를 포함하고 있어 사용자의 요구사항과 가장 잘 맞습니다. 임산부 금기 성분 경고가 명확히 없어 우선 순위로 고려됩니다. (목표 부합 70% - 비타민 C 포함, 안전성 70% - 임산부 금기 명확한 경고 없음, 효능 70% - 비타민 C, D 포함)"
                 },
                 {
                   "rank": 2,
                   "name": "일화닥터삼",
                   "score": 62,
                   "percentage": "62%",
                   "reason": "비타민 C를 포함하고 있으나, 인삼 성분으로 인해 임산부에게 주의가 필요할 수 있어 우선순위가 낮습니다. (목표 부합 70% - 비타민 C 포함, 인삼 성분으로 인한 잠재적 위험, 안전성 50% - 인삼 성분으로 인한 주의 필요, 효능 70% - 비타민 C 포함)"
                 },
                 {
                   "rank": 3,
                   "name": "밀크씨슬옥타플러스",
                   "score": 33,
                   "percentage": "33%",
                   "reason": "사용자의 요구사항인 비타민 C를 포함하지 않고, 제품의 주요 효능이 임산부의 필요와 관련이 적습니다. 임산부에게 직접적인 금기 사항은 없으나, 요구사항 충족도가 낮습니다. (목표 부합 10% - 비타민 C 미포함, 안전성 60% - 임산부 금기 명확한 경고 없음, 효능 20% - 요구사항과 무관한 성분)"
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
            - Respond only in Korean.
            
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
        ObjectMapper mapper = new ObjectMapper();
        RankedResponseDTO rankedResponse = mapper.readValue(jsonOutput, RankedResponseDTO.class);

        // 리스트만 반환
        return rankedResponse.getRanked_results();
    }
}
