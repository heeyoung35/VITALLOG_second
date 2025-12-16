package com.vitallog.config;

import com.google.genai.Client;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:model.properties")
public class GeminiConfig {
    @Value("${gemini.api-key}")
    private String key;

    @Value("${gemini.model}")
    private String model;

    @Bean
    public Client GeminiModel(){
        return Client.builder()
            .apiKey(key)
            .build();
    }
}
