package com.vitallog.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static java.time.Duration.ofSeconds;

@Configuration
@PropertySource("classpath:model.properties")
public class VectorizationModelConfig {
    @Value("${huggingface.token}")
    private String token;

    @Value("${huggingface.embedding-model}")
    private String embedModel;

    @Bean
    public EmbeddingModel embeddingModel(){
        EmbeddingModel embeddingModel = HuggingFaceEmbeddingModel.builder()
            .accessToken(token)
            .modelId(embedModel)
            .waitForModel(true)
            .timeout(ofSeconds(60))
            .build();

        return embeddingModel;
    }
}
