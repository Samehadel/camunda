package com.practice.camunda.service;

import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;

import java.util.List;
import java.util.Map;

public interface CamundaService {

    ProcessInstanceWithVariablesDto startProcess(final String businessKey) throws ApiException;

    TaskDto getTask(final String taskId) throws ApiException;

    List<ProcessInstanceDto> getPendingProcessInstances() throws ApiException;

    void completeTask(final TaskDto task, final Map<String, VariableValueDto> map) throws ApiException;

    void completeTask(final TaskDto task) throws ApiException;

    void validateTaskCandidateGroup(final String taskId) throws ApiException;

    String getBusinessKey(String processInstanceId) throws ApiException;

    ProcessInstanceDto getProcessInstance(String processInstanceId) throws ApiException;

    List<TaskDto> getPendingTasks(String businessKey) throws ApiException;
}
