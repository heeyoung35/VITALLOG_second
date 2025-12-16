package com.vitallog.recommend;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.vitallog.config.GeminiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@Service
@SpringBootApplication
public class ResponseGemini {
    private final GeminiConfig geminiConfig;
    @Autowired
    public ResponseGemini(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
    }

    public String generateText(String prompt) {
        Client client = geminiConfig.GeminiModel();

        if (client == null || prompt.isEmpty()) {
            return "Gemini Client가 초기화 되지 않았습니다.";
        }
        GenerateContentResponse response = client.models.generateContent(
            geminiConfig.getModel(),
            prompt,
            null
        );
        return response.text();
    }

}
