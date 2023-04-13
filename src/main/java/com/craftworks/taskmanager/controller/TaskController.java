package com.craftworks.taskmanager.controller;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.TaskDto;
import com.craftworks.taskmanager.exception.TaskAccessException;
import com.craftworks.taskmanager.exception.TaskNotFoundException;
import com.craftworks.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller class for Taskmanagement System
 * <p>
 *     This class is responsible for handling CRUD requests related to Task management system and delegating the request to the service layer.
 *     It handles the response to be sent back to the client.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks
     * <p>
     *     This method is responsible for handling GET request to get all tasks.
     *     It delegates the request to the service layer and returns the response to the client.
     *     If any error occurs while getting the tasks, it returns an error response to the client.
     *     If no tasks are found, it returns an empty list.
     *     If tasks are found, it returns a list of tasks.
     * </p>
     * @return List of all tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        logger.info("Received request to get all tasks");
        try {
            List<TaskDto> taskDtos = taskService.getAllTasks();
            logger.info("Returning all tasks");
            return ResponseEntity.ok(taskDtos);
        } catch (TaskAccessException ex) {
            logger.error("Error occurred while getting all tasks: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get task by id
     * <p>
     *     This method is responsible for handling GET request to get a task by id.
     *     It delegates the request to the service layer and returns the response to the client.
     *     If any error occurs while getting the task, it returns an error response to the client.
     *     If no task is found, it returns a not found response to the client.
     *     If task is found, it returns the task.
     *     It also returns the location of the task in the response header.
     * </p>
     *
     * @param taskId        Id of the task to be retrieved
     * @param uriBuilder    UriBuilder to build the location of the task
     * @return              Task with the given id
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable @NotNull Long taskId,
                                           UriComponentsBuilder uriBuilder) {
        logger.info("Received request to get Task with id: {}", taskId);
        try {
            TaskDto taskDto = taskService.getTaskById(taskId);
            logger.info("Returning Task: {}", taskDto);
            URI location = uriBuilder.path("/task/{taskId}").buildAndExpand(taskDto.getId()).toUri();
            return ResponseEntity.ok().location(location).body(taskDto);
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a task
     * <p>
     *     This method is responsible for handling POST request to create a task.
     *     It delegates the request to the service layer and returns the response to the client.
     *     If any error occurs while creating the task, it returns an error response to the client.
     *     If task is created successfully, it returns the created task.
     *     It also returns the location of the task in the response header.
     *     It handles validation errors and returns a bad request response to the client in case of a invalid task.
     * </p>
     * @param taskDto           Task to be created
     * @param bindingResult     BindingResult to handle validation errors
     * @param uriBuilder        UriBuilder to build the location of the task
     * @return                  Created task
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateTaskDto taskDto,
                                              BindingResult bindingResult,
                                              UriComponentsBuilder uriBuilder) {
        logger.info("Received request to create Task with id: {}", taskDto);
        // Handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred while updating task: {}", taskDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            TaskDto createdTaskDto = taskService.createTask(taskDto);
            URI location = uriBuilder.path("/task/{taskId}").buildAndExpand(createdTaskDto.getId()).toUri();
            logger.info("Returning created Task: {}", createdTaskDto);
            return ResponseEntity.status(HttpStatus.CREATED).location(location).body(createdTaskDto);
        } catch (Exception ex) {
            logger.error("An error occurred while creating task: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a task
     * <p>
     *     This method is responsible for handling PUT request to update a task.
     *     It delegates the request to the service layer and returns the response to the client.
     *     If any error occurs while updating the task, it returns an error response to the client.
     *     If no task is found, it returns a not found response to the client.
     *     If task is updated successfully, it returns the updated task.
     *     It also returns the location of the task in the response header.
     *     It handles validation errors and returns a bad request response to the client in case of a invalid task.
     * </p>
     * @param taskId        Id of the task to be updated
     * @param taskDto       Task to be updated
     * @param bindingResult BindingResult to handle validation errors
     * @param uriBuilder    UriBuilder to build the location of the task
     * @return              Updated task
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable @NotNull Long taskId,
                                              @RequestBody @Valid TaskDto taskDto,
                                              BindingResult bindingResult,
                                              UriComponentsBuilder uriBuilder) {
        logger.info("Received request to update Task with id: {}", taskId);
        //handle validation errors and return error response
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred while updating task: {}", taskDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            TaskDto taskDtoResponse = taskService.updateTask(taskId, taskDto);
            URI uri = uriBuilder.path("/task/{taskId}").buildAndExpand(taskDtoResponse.getId()).toUri();
            logger.info("Returning updated Task: {}", taskDtoResponse);
            return ResponseEntity.ok().location(uri).body(taskDtoResponse);
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("An error occurred while updating task: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(taskDto);
        }
    }

    /**
     * Delete a task
     * <p>
     *     This method is responsible for handling DELETE request to delete a task.
     *     It delegates the request to the service layer and returns the response to the client.
     *     If any error occurs while deleting the task, it returns an error response to the client.
     *     If no task is found, it returns a not found response to the client.
     *     If task is deleted successfully, it returns a void response.
     * </p>
     * @param taskId    Id of the task to be deleted
     * @return          Empty response
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable @NotNull Long taskId) {
        logger.info("Received request to delete Task with id: {}", taskId);
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