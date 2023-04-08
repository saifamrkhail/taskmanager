package com.craftworks.taskmanager.service;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.TaskDto;
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

    public List<TaskDto> getAllTasks() {
        try {
            Optional<List<Task>> tasks = Optional.of(taskRepository.findAll());
            logger.info("Retrieving all tasks");
            return taskMapper.taskListToTaskDtoList(tasks.get());
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while getting all tasks: {}", ex.getMessage());
            throw new TaskAccessException("Failed to get all tasks", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Error occurred while getting all tasks: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public TaskDto getTaskById(Long taskId) {
        try {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent()) {
                logger.info("Task found with id: {}", taskId);
                return taskMapper.taskToTaskDto(task.get());
            } else {
                logger.info("Task not found with id: {}", taskId);
                throw new TaskNotFoundException("Task not found with id: " + taskId, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while retrieving task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TaskDto createTask(CreateTaskDto taskDto) {
        TaskDto createdTaskDto;
        try {
            Task task = taskMapper.createTaskDtoToEntity(taskDto, new Task());
            task = taskRepository.save(task);
            createdTaskDto = taskMapper.taskToTaskDto(task);
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

    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            logger.error("Task not found: {}", taskDto);
            throw new TaskNotFoundException("Task not found", HttpStatus.NOT_FOUND);
        }
        Task task = optionalTask.get();
        task = taskMapper.taskDtoToEntity(taskDto, task);
        try {
            task = taskRepository.save(task);
            TaskDto updatedTaskDto = taskMapper.taskToTaskDto(task);
            logger.info("Updated task with id: {}", taskId);
            return updatedTaskDto;
        } catch (DataAccessException ex) {
            logger.error("A DataAccessException occurred while updating task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
