package com.practice.camunda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.dto.CompleteTaskDto;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceQueryDto;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.StartProcessInstanceDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.TaskQueryDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.springboot.CamundaApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamundaServiceImpl implements CamundaService {
    private final CamundaApi camundaApi;
    private final UserService userService;
    private final String CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY = "cashback_campaign_process";

    @Override
    public ProcessInstanceWithVariablesDto startProcess(final String businessKey) throws ApiException {
        StartProcessInstanceDto processInstanceDto = new StartProcessInstanceDto()
                .businessKey(businessKey);
        return camundaApi.processDefinitionApi().startProcessInstanceByKey(CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY, processInstanceDto);
    }

    @Override
    public TaskDto getTask(final String taskId) throws ApiException {
        TaskQueryDto taskQueryDto = new TaskQueryDto()
                .processDefinitionKey(CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY)
                .taskId(taskId);
        List<TaskDto> tasks = camundaApi.taskApi()
                .queryTasks(0, Integer.MAX_VALUE, taskQueryDto);
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }
        return tasks.getFirst();
    }

    @Override
    public List<ProcessInstanceDto> getPendingProcessInstances() throws ApiException {
        var queryDto = new ProcessInstanceQueryDto().processDefinitionKey(CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY);
        return camundaApi.processInstanceApi().queryProcessInstances(0, Integer.MAX_VALUE, queryDto);
    }

    @Override
    public void completeTask(final TaskDto task, final Map<String, VariableValueDto> map) throws ApiException {
        try {
            if (task == null) {
                throw new RuntimeException("Task is null");
            }
            var completeTaskDto = new CompleteTaskDto().variables(map);
            camundaApi.taskApi().complete(task.getId(), completeTaskDto);
        } finally {
            log.info("Task completed successfully");
        }
    }

    @Override
    public void completeTask(final TaskDto task) throws ApiException {
        completeTask(task, null);
    }

    @Override
    public void validateTaskCandidateGroup(final String taskId) throws ApiException {
        if (taskId == null) {
            throw new RuntimeException("Task ID is null");
        }
        List<String> userRoles = userService.getUserRoles();
        List<String> tasksIds = getPendingTasksForCandidateGroup(userRoles);
        if (!tasksIds.contains(taskId)) {
            throw new RuntimeException("User is not authorized to complete this task");
        }

    }

    private List<String> getPendingTasksForCandidateGroup(List<String> candidateGroups) throws ApiException {
        TaskQueryDto taskQueryDto = new TaskQueryDto().candidateGroups(candidateGroups)
                .processDefinitionKey(CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY);
        List<TaskDto> tasks = camundaApi.taskApi().queryTasks(0, Integer.MAX_VALUE, taskQueryDto);
        return tasks.stream()
                .map(TaskDto::getId)
                .toList();
    }

    @Override
    public String getBusinessKey(String processInstanceId) throws ApiException {
        ProcessInstanceDto processInstance = camundaApi.processInstanceApi().getProcessInstance(processInstanceId);
        return processInstance.getBusinessKey();
    }

    @Override
    public ProcessInstanceDto getProcessInstance(String processInstanceId) throws ApiException {
        return camundaApi.processInstanceApi().getProcessInstance(processInstanceId);
    }

    @Override
    public List<TaskDto> getPendingTasks(String businessKey) throws ApiException {
        TaskQueryDto taskQueryDto = new TaskQueryDto()
                .processDefinitionKey(CASHBACK_CAMPAIGN_PROCESS_DEFINITION_KEY)
                .processInstanceBusinessKey(businessKey);
        return camundaApi.taskApi().queryTasks(0, Integer.MAX_VALUE, taskQueryDto);
    }

}
