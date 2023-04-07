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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> home() {
        logger.info("Received request to get home page");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Craftworks Task Manager");
        response.put("getAllTasks", "/tasks");
        logger.info("Returning home page");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UpdateTaskDto>> getAllTasks() {
        logger.info("Received request to get all tasks");
        try {
            List<UpdateTaskDto> taskDtos = taskService.getAllTasks();
            logger.info("Returning all tasks");
            return ResponseEntity.ok(taskDtos);
        } catch (TaskAccessException ex) {
            logger.error("Error occurred while getting all tasks: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<UpdateTaskDto> getTask(@PathVariable @NotNull Long taskId,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriBuilder) {
        logger.info("Received request to get Task with id: {}", taskId);
        if (bindingResult.hasErrors()) {
            // Handle validation errors and return error response
            return ResponseEntity.badRequest().build();
        }
        try {
            UpdateTaskDto taskDto = taskService.getTaskById(taskId);
            logger.info("Returning Task: {}", taskDto);
            URI uri = uriBuilder.path("/task/{taskId}").buildAndExpand(taskDto.getId()).toUri();
            return ResponseEntity.ok().location(uri).body(taskDto);
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }  catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UpdateTaskDto> createTask(@RequestBody @Valid CreateTaskDto taskDto,
                                                    BindingResult bindingResult,
                                                    UriComponentsBuilder uriBuilder) {
        logger.info("Received request to create Task with id: {}", taskDto);
        // Handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred while updating task: {}", taskDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            UpdateTaskDto createdTaskDto = taskService.createTask(taskDto);
            URI location = uriBuilder.path("/task/{taskId}").buildAndExpand(createdTaskDto.getId()).toUri();
            logger.info("Returning created Task: {}", createdTaskDto);
            return ResponseEntity.status(HttpStatus.CREATED).location(location).body(createdTaskDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<UpdateTaskDto> updateTask(@PathVariable @NotNull Long taskId,
                                                    @RequestBody @Valid UpdateTaskDto taskDto,
                                                    BindingResult bindingResult,
                                                    UriComponentsBuilder uriBuilder) {
        logger.info("Received request to update Task with id: {}", taskId);
        //handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred while updating task: {}", taskDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            UpdateTaskDto taskDtoResponse = taskService.updateTask(taskId, taskDto);
            URI uri = uriBuilder.path("/task/{taskId}").buildAndExpand(taskDtoResponse.getId()).toUri();
            logger.info("Returning updated Task: {}", taskDtoResponse);
            return ResponseEntity.ok().location(uri).body(taskDtoResponse);
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(taskDto);
        }
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable @NotNull Long taskId,
                                           BindingResult bindingResult) {
        logger.info("Received request to delete Task with id: {}", taskId);
        // Handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            taskService.deleteTask(taskId);
            logger.info("Task with id: {} deleted successfully", taskId);
            return ResponseEntity.ok().build();
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}