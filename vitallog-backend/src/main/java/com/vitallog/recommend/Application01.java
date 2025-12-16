package com.vitallog.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vitallog.recommend.service.NativeRAGService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class Application01 {
    public static void main(String[] args) throws IOException {
        //ApplicationContext context = SpringApplication.run(VectorizationModel.class, args);
        //VectorizationModel embeddingModel = context.getBean(VectorizationModel.class);
        //float[] response = embeddingModel.intoVector("한국에 대해서 설명하겠습니다.");

        //ApplicationContext context = SpringApplication.run(CreateCollection.class, args);
        //CreateCollection embeddingModel = context.getBean(CreateCollection.class);
        //embeddingModel.create();

        //ApplicationContext context = SpringApplication.run(InsertVector.class, args);
        //InsertVector embeddingModel = context.getBean(InsertVector.class);
        //embeddingModel.insert();

        //ApplicationContext context = SpringApplication.run(SearchVector.class, args);
        //SearchVector embeddingModel = context.getBean(SearchVector.class);
        //List<Object> searchList = embeddingModel.search("비타민E 추천해줘");
        //String result = searchList.stream()
        //    .map(Object::toString)            // 각 요소를 문자열로 변환
        //    .collect(Collectors.joining(",")); // 쉼표로 연결
        //
        //System.out.println(result);

        //ApplicationContext context = SpringApplication.run(NativeRAG.class, args);
        //NativeRAG embeddingModel = context.getBean(NativeRAG.class);
        //String response = embeddingModel.generateText("심장 질환에 좋은 관절 영양제 추천해줘 ");
        //System.out.println(response);
    }
}
