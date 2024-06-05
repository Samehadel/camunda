package com.practice.camunda.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaClientConfig {

    @Bean
    public ExternalTaskClient externalTaskClient() {
        return ExternalTaskClient.create()
                .baseUrl("http://localhost:8082/engine-rest")
                .asyncResponseTimeout(10000)
                .build();
    }
}
