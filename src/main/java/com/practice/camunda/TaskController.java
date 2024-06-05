package com.practice.camunda;

import com.practice.camunda.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PutMapping("/complete/{task_id}")
    public ResponseEntity<Void> completeTask(@RequestParam(name = "approved", required = false) final Boolean approve,
                                             @PathVariable(name = "task_id") final String taskId) throws ApiException {
        taskService.completeTask(taskId, approve);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending/{business_key}")
    public ResponseEntity<List<String>> getPendingTasks(@PathVariable(name = "business_key") final String businessKey) throws ApiException {
        List<String> tasks = taskService.getPendingTasks(businessKey);
        return ResponseEntity.ok(tasks);
    }
}

