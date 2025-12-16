package com.vitallog.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


@Configuration
@PropertySource("classpath:db.properties")
public class ZillizDBConfig {
    @Value("${vectordb.uri}")
    private String uri;

    @Value("${vectordb.token}")
    private String token;

    @Bean
    public MilvusClientV2 milvusClient() {
        ConnectConfig connectConfig = ConnectConfig.builder()
            .uri(uri)  // Milvus 주소
            .token(token)            // 인증 토큰 또는 user:password
            .secure(true)
            .build();

        return new MilvusClientV2(connectConfig);
    }
}
