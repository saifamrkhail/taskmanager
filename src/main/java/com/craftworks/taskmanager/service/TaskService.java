package com.craftworks.taskmanager.service;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.UpdateTaskDto;
import com.craftworks.taskmanager.entity.Task;
import com.craftworks.taskmanager.exception.TaskAccessException;
import com.craftworks.taskmanager.exception.TaskNotFoundException;
import com.craftworks.taskmanager.mapper.TaskMapper;
import com.craftworks.taskmanager.repository.TaskRepository;
import com.craftworks.taskmanager.scheduler.CraftworksTaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
        List<Task> tasks = taskRepository.findAll();
        if (CollectionUtils.isEmpty(tasks)) {
            logger.info("Tasks not found");
            return null;
        }
        return tasks.stream().map(taskMapper::toUpdateDto).collect(Collectors.toList());
    }

    public ResponseEntity<UpdateTaskDto> getTaskById(Long taskId, UriComponentsBuilder uriBuilder) {
        UpdateTaskDto foundTaskDto = null;
        try {
            Optional<Task> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                foundTaskDto = taskMapper.toUpdateDto(optionalTask.get());
                URI uri = uriBuilder.path("/tasks/{taskId}").buildAndExpand(foundTaskDto.getId()).toUri();
                logger.info("Found task with id: {}", taskId);
                return ResponseEntity.ok().location(uri).body(foundTaskDto);


            } else {
                logger.info("Task not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("An error occurred while fetching task: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(foundTaskDto);
        }
    }

    public ResponseEntity<UpdateTaskDto> createTask(CreateTaskDto taskDto, UriComponentsBuilder uriBuilder) {
        UpdateTaskDto createdTaskDto = null;
        try {
            Task task = taskRepository.save(taskMapper.createTaskDtoToEntity(taskDto, new Task()));
            createdTaskDto = taskMapper.toUpdateDto(task);

            URI location = uriBuilder.path("/tasks/{taskId}").buildAndExpand(createdTaskDto.getId()).toUri();
            return ResponseEntity.created(location).body(createdTaskDto);

        } catch (Exception ex) {
            logger.error("An error occurred while saving task: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createdTaskDto);
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
        }  catch (DataAccessException ex) {
            logger.error("An error occurred while updating task: {}", ex.getMessage());
            throw new TaskAccessException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("An error occurred while updating task: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
        logger.info("Deleted task with id: {}", taskId);
    }
}
