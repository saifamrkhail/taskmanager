package com.craftworks.taskmanager.controller;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.UpdateTaskDto;
import com.craftworks.taskmanager.exception.TaskAccessException;
import com.craftworks.taskmanager.exception.TaskNotFoundException;
import com.craftworks.taskmanager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
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
        logger.info("Received request to update Task with id: {}", taskId);
        //handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred while updating task with id: {}", taskId);
            return ResponseEntity.badRequest().build();
        }

        try {
            UpdateTaskDto taskDtoResponse = taskService.updateTask(taskId, taskDto);
            URI uri = uriBuilder.path("/tasks/{taskId}").buildAndExpand(taskDtoResponse.getId()).toUri();
            logger.info("Returning updated Task with id: {}", taskId);
            return ResponseEntity.ok().location(uri).body(taskDtoResponse);
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(taskDto);
        }
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