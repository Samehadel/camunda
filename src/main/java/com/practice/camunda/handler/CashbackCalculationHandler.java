package com.practice.camunda.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CashbackCalculationHandler {
    private final ExternalTaskClient externalTaskClient;

    @PostConstruct
    public void subscribe() {
        externalTaskClient.subscribe("cashback_calculation")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    log.info("Running external task id [{}]", externalTask.getId());
                    final var campaignId = Long.valueOf(externalTask.getBusinessKey());
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}
