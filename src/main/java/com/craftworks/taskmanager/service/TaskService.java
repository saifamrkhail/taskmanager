package com.craftworks.taskmanager.service;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.UpdateTaskDto;
import com.craftworks.taskmanager.entity.Task;
import com.craftworks.taskmanager.exception.TaskAccessException;
import com.craftworks.taskmanager.exception.TaskNotFoundException;
import com.craftworks.taskmanager.mapper.TaskMapper;
import com.craftworks.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<UpdateTaskDto> getAllTasks() {
        try {
            Optional<List<Task>> tasks = Optional.of(taskRepository.findAll());
            logger.info("Retrieving all tasks");
            return tasks.get().stream()
                    .map(taskMapper::toUpdateDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while getting all tasks: {}", ex.getMessage());
            throw new TaskAccessException("Failed to get all tasks", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Error occurred while getting all tasks: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public UpdateTaskDto getTaskById(Long taskId) {
        try {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent()) {
                logger.info("Task found with id: {}", taskId);
                return taskMapper.toUpdateDto(task.get());
            } else {
                logger.info("Task with id {} not found", taskId);
                throw new TaskNotFoundException("Task with id:" + taskId + "not found", HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while creating task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("An error occurred while retrieving task with id: {} {}", taskId, ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public UpdateTaskDto createTask(CreateTaskDto taskDto) {
        UpdateTaskDto createdTaskDto;
        try {
            Task task = taskRepository.save(taskMapper.createTaskDtoToEntity(taskDto, new Task()));
            createdTaskDto = taskMapper.toUpdateDto(task);
            logger.info("Created task: {}", createdTaskDto);
            return createdTaskDto;
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while creating task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("An error occurred while creating task: {} ", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public UpdateTaskDto updateTask(Long taskId, UpdateTaskDto taskDto) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            logger.error("Task not found: {}", taskDto);
            throw new TaskNotFoundException("Task not found", HttpStatus.NOT_FOUND);
        }
        Task task = optionalTask.get();
        task = taskMapper.updateTaskDtoToEntity(taskDto, task);
        try {
            task = taskRepository.save(task);
            UpdateTaskDto updatedTaskDto = taskMapper.toUpdateDto(task);
            logger.info("Updated task with id: {}", taskId);
            return updatedTaskDto;
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while updating task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("An error occurred while updating task: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void deleteTask(Long taskId) {
        try {
            taskRepository.deleteById(taskId);
            logger.info("Deleted task with id: {}", taskId);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Task not found with id: {}", taskId);
            throw new EntityNotFoundException("Task not found with id " + taskId);
        } catch (DataAccessException e) {
            logger.error("A DataAccessException error occurred while deleting task with id: {}", taskId);
            throw new TaskAccessException("An error occurred while deleting task with id: " + taskId, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("An error occurred while deleting task with id: {}", taskId);
            throw new RuntimeException(e.getMessage());
        }
    }
}
