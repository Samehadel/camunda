package com.practice.camunda.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointRedeemHandler {
    private final ExternalTaskClient externalTaskClient;

    @PostConstruct
    public void subscribe() {
        externalTaskClient.subscribe("points_redemption")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    log.info("Running external task id [{}]", externalTask.getId());
                    redeemPoints(externalTask, externalTaskService);
                })
                .open();
    }

    private void redeemPoints(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        log.info("Redeeming points");
        externalTaskService.complete(externalTask);
    }
}
