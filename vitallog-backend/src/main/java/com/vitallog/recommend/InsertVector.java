package com.vitallog.recommend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.vitallog.config.VectorizationModelConfig;
import dev.langchain4j.data.embedding.Embedding;

import dev.langchain4j.model.output.Response;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.InsertReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@SpringBootApplication
public class InsertVector {
    private final MilvusClientV2 client;
    private final VectorizationModelConfig vectorizationModel;
    private final List<JsonObject> dataList = new ArrayList<>();

    @Autowired
    public InsertVector(VectorizationModelConfig vectorizationModel, MilvusClientV2 client) {
        this.vectorizationModel = vectorizationModel;
        this.client = client;
    }

    public void insert() throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> nuts = mapper.readValue(
                    new File("src/main/resources/supplements3.json"),
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
                return;
            }

            InsertReq insertReq = InsertReq.builder()
                    .collectionName("supplement")
                    .data(dataList)
                    .build();

            client.insert(insertReq);
            System.out.println("데이터 삽입 완료!");

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            client.close();
        }
    }

    private void embedAndAdd(JsonObject jsonRow, String fieldName, String text) {
        if (text != null && !text.isEmpty()) {
            long start = System.nanoTime();
            String preprocessedText = preprocess(text);
            Response<Embedding> response = vectorizationModel.embeddingModel().embed(preprocessedText);
            Embedding embedding = response.content();
            if (embedding != null) {
                float[] vector = embedding.vector();
                jsonRow.add(fieldName, convertFloatArrayToJsonArray(vector));
                long end = System.nanoTime();
                timer(start, end);
            }
        }
    }

    private JsonArray convertFloatArrayToJsonArray(float[] floatArray) {
        JsonArray jsonArray = new JsonArray();
        for (float v : floatArray) {
            jsonArray.add(v);
        }
        return jsonArray;
    }

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

    private void timer(long start, long end) {
        long totalMillis = (end - start) / 1_000_000;
        long minutes = totalMillis / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long millis = totalMillis % 1000;

        System.out.printf("걸린 시간: %d분 %d초 %d밀리초%n", minutes, seconds, millis);
    }
}
