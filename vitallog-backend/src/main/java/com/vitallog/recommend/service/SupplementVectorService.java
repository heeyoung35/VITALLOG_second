package com.vitallog.recommend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vitallog.config.VectorizationModelConfig;
import com.vitallog.recommend.CreateCollection;
import com.vitallog.recommend.repository.SupplementVectorRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.output.Response;
import io.milvus.v2.service.vector.request.data.FloatVec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupplementVectorService {
    private final List<JsonObject> dataList = new ArrayList<>();
    private final VectorizationModelConfig vectorModel;
    private final SupplementVectorRepository supplementVectorRepository;

    // 1. Supplement Collection 생성
    public void createSupplementCollection(){
        CreateCollection createCollection = new CreateCollection();
        createCollection.create();
    }

    // 2. Supplement DataSet 삽입
    public List<JsonObject> insertSupplements() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> nuts = mapper.readValue(
            new File("src/main/resources/supplements.json"),
            new TypeReference<List<Map<String, Object>>>() {}
        );

        int count = 1;
        int len =  nuts.size();
        for (Map<String, Object> nut : nuts) {
            JsonObject jsonRow = new JsonObject();
            // nut_no 추가 (Integer나 Long 처리)
            Object nutNoObj = nut.get("nutNo");
            if (nutNoObj instanceof Number) {
                jsonRow.addProperty("nut_no", ((Number) nutNoObj).longValue());
            } else if (nutNoObj != null) {
                jsonRow.addProperty("nut_no", Long.parseLong(nutNoObj.toString()));
            }

            // 텍스트 필드 임베딩 및 추가
            embedAndAdd(jsonRow, "nut_name", nut.get("nutName").toString());
            embedAndAdd(jsonRow, "primary_fncity", nut.get("primaryFnclty").toString());
            embedAndAdd(jsonRow, "warning", nut.get("warning").toString());
            embedAndAdd(jsonRow, "raw_name", nut.get("rawName").toString());
            JsonObject metadata   = new JsonObject();
            metadata.addProperty("nut_no", nut.get("nutNo").toString());
            metadata.addProperty("nut_name", nut.get("nutName").toString());
            metadata.addProperty("nut_mthd", nut.get("nutMthd").toString());
            metadata.addProperty("primary_fnclty", preprocess(nut.get("primaryFnclty").toString()));
            metadata.addProperty("warning", preprocess(nut.get("warning").toString()));
            metadata.addProperty("storage_mthd", nut.get("storageMthd").toString());
            metadata.addProperty("raw_name", nut.get("rawName").toString());
            metadata.addProperty("price", nut.get("price").toString());
            jsonRow.add("metadata", metadata);

            dataList.add(jsonRow);
            System.out.println("(" + count++ + "/" + len + ")번째 행 완료!");
        }

        if (dataList.isEmpty()) {
            System.out.println("No data to insert.");
            return new ArrayList<>();
        }

        supplementVectorRepository.insert(dataList);

        return dataList;
    }

    // 2-1. 임베딩
    private void embedAndAdd(JsonObject jsonRow, String fieldName, String text) {
        if (text != null && !text.isEmpty()) {
            long start = System.nanoTime();
            String preprocessedText = preprocess(text);
            Response<Embedding> response = vectorModel.embeddingModel().embed(preprocessedText);
            Embedding embedding = response.content();
            if (embedding != null) {
                float[] vector = embedding.vector();
                jsonRow.add(fieldName, convertFloatArrayToJsonArray(vector));
                long end = System.nanoTime();
                timer(start, end);
            }
        }
    }

    // 2-2. Json 배열로 변환
    private JsonArray convertFloatArrayToJsonArray(float[] floatArray) {
        JsonArray jsonArray = new JsonArray();
        for (float v : floatArray) {
            jsonArray.add(v);
        }
        return jsonArray;
    }

    // 2-3. 문자열 전처리
    private String preprocess(String inputText){
        String preprocessedText = inputText
            // (가)(나)(다) 등 제거
            .replaceAll("\\([가-힣]\\)", "")
            // ①~⑳ 제거
            .replaceAll("[\\u2460-\\u2473]", "")
            // (1)(2)(10) 등 숫자 괄호 제거
            .replaceAll("\\(\\d+\\)", "")
            // 줄바꿈 → 공백
            .replaceAll("[\\r\\n]+", " ")
            // 여러 공백 → 하나
            .replaceAll("\\s+", " ")
            // 앞뒤 공백 제거
            .trim();

        return preprocessedText;
    }

    // 2-4. 타이머
    private void timer(long start, long end) {
        long totalMillis = (end - start) / 1_000_000;
        long minutes = totalMillis / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long millis = totalMillis % 1000;

        System.out.printf("걸린 시간: %d분 %d초 %d밀리초%n", minutes, seconds, millis);
    }

    // 3. Supplement 검색
    private List<Object> searchSupplements(String inputText){
        Response<Embedding> response = vectorModel.embeddingModel().embed(inputText);
        Embedding embedding = response.content();
        float[] vector = embedding.vector();

        return supplementVectorRepository.search(vector);
    }
}
