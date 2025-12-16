package com.vitallog.recommend;

import com.vitallog.config.VectorizationModelConfig;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import static java.time.Duration.ofSeconds;

@Service
@ComponentScan(basePackages = {"com.vitallog.config"})
@SpringBootApplication
public class VectorizationModel {
    private final VectorizationModelConfig vectorModel;

    @Autowired
    public VectorizationModel(VectorizationModelConfig vectorModel) {
        this.vectorModel = vectorModel;
    }

    public float[] intoVector(String inputText) {
        long start = System.nanoTime();
        Response<Embedding> response = vectorModel.embeddingModel().embed(inputText);

        Embedding embedding = response.content();
        float[] vector = embedding.vector();
        long end = System.nanoTime();

        long totalMillis = (end - start) / 1_000_000;
        long minutes = totalMillis / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        long millis = totalMillis % 1000;


        System.out.println("Embedding Vector: " + Arrays.toString(vector));
        System.out.println("Dimension: " + vector.length);
        System.out.printf("걸린 시간: %d분 %d초 %d밀리초%n", minutes, seconds, millis);

        return vector;
    }
}
