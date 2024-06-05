package com.practice.camunda.service;

import com.practice.camunda.ProcessVariables;
import lombok.RequiredArgsConstructor;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final CamundaService camundaService;

    public void completeTask(String taskId, Boolean approve) throws ApiException {
        TaskDto task = camundaService.getTask(taskId);
        if (task == null) {
            throw new RuntimeException("Task is null");
        }
        camundaService.validateTaskCandidateGroup(task.getId());
        completeTaskWithVariables(task, approve);
    }

    private void completeTaskWithVariables(TaskDto task, Boolean status) throws ApiException {
        if (status == null) {
            camundaService.completeTask(task);
            return;
        }
        final VariableValueDto action = new VariableValueDto().type("Boolean").value(status);
        Map<String, VariableValueDto> variables = Map.of(ProcessVariables.APPROVED, action);
        camundaService.completeTask(task, variables);
    }

    public List<String> getPendingTasks(String businessKey) throws ApiException {
        List<TaskDto> tasks = camundaService.getPendingTasks(businessKey);
        return tasks.stream().map(TaskDto::getId).collect(Collectors.toList());
    }
}
