package com.practice.camunda.handler;

import com.practice.camunda.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.springframework.stereotype.Component;

import static com.practice.camunda.ProcessVariables.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageNotificationHandler {
    private static final String NOTIFICATION_QUEUE = "email_notification";
    private final ExternalTaskClient externalTaskClient;

    @PostConstruct
    public void subscribe() {
        externalTaskClient.subscribe(NOTIFICATION_QUEUE)
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    log.info("Running external task id [{}]", externalTask.getId());
                    sendNotificationEmail(externalTask);
                    externalTaskService.complete(externalTask);
                })
                .open();
    }

    private void sendNotificationEmail(ExternalTask externalTask) {
        String actionGroup = externalTask.getVariable(ACTION_GROUP);
        Boolean approved = getApprovalStatus(externalTask);

        if (Role.CASHBACK_PRODUCT_DEVELOPMENT_MAKER.equals(actionGroup)) {
            sendNotificationEmail(externalTask, Role.CASHBACK_PRODUCT_DEVELOPMENT_CHECKER);
        } else if (Role.CASHBACK_PRODUCT_DEVELOPMENT_CHECKER.equals(actionGroup)) {
            if (approved) {
                sendNotificationEmail(externalTask, externalTask.getVariable(FORWARD_GROUP));
            } else {
                sendNotificationEmail(externalTask, Role.CASHBACK_PRODUCT_DEVELOPMENT_MAKER);
            }
        } else if (Role.CASHBACK_CARDS_CHECKER.equals(actionGroup)) {
            if (!approved) {
                sendNotificationEmail(externalTask, Role.CASHBACK_PRODUCT_DEVELOPMENT_CHECKER);
            }
        }

        log.info("Action group [{}] is approved [{}]", actionGroup, approved);
    }

    private void sendNotificationEmail(ExternalTask externalTask, String receivingGroup) {
        log.info("Sending notification email to [{}]", receivingGroup);

    }

    private Boolean getApprovalStatus(ExternalTask externalTask) {
        final Boolean approved = externalTask.getVariable(APPROVED);
        return approved == null ? Boolean.FALSE : approved;
    }
}
