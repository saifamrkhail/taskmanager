package com.craftworks.taskmanager.controller;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.UpdateTaskDto;
import com.craftworks.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<UpdateTaskDto>> getAllTasks() {
        List<UpdateTaskDto> taskDtos = taskService.getAllTasks();
        if (CollectionUtils.isEmpty(taskDtos)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<UpdateTaskDto> getTask(@PathVariable @NotNull Long taskId,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriBuilder) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors and return error response
            return ResponseEntity.badRequest().build();
        }
        return taskService.getTaskById(taskId, uriBuilder);
    }

    @PostMapping
    public ResponseEntity<UpdateTaskDto> createTask(@RequestBody @Valid CreateTaskDto taskDto,
                                                    BindingResult bindingResult,
                                                    UriComponentsBuilder uriBuilder) {

        if (bindingResult.hasErrors()) {
            // Handle validation errors and return error response
            return ResponseEntity.badRequest().build();
        }

        return taskService.createTask(taskDto, uriBuilder);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<UpdateTaskDto> updateTask(@PathVariable @NotNull Long taskId,
                                                    @RequestBody @Valid UpdateTaskDto taskDto,
                                                    BindingResult bindingResult,
                                                    UriComponentsBuilder uriBuilder) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors and return error response
            return ResponseEntity.badRequest().build();
        }

        return taskService.updateTask(taskId, taskDto, uriBuilder);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable @NotNull Long taskId,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors and return error response
            return ResponseEntity.badRequest().build();
        }
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}