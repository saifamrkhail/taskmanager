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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service of Taskmanagement System.
 * <p>
 *     This class is responsible for handling CRUD requests related to Task management system and delegating the request to the repository layer.
 *     It handles the response to be sent back to the controller.
 *     It also handles any exceptions that may occur while performing the operation.
 *     It also handles any exceptions that may occur while performing the operation.
 * </p>
 */
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

    /**
     * Get all tasks
     * <p>
     *     This method is responsible for handling GET request to get all tasks.
     *     It delegates the request to the repository layer and returns the response to the controller.
     *     If any error occurs while getting the tasks, it returns an error response to the controller.
     *     If no tasks are found, it returns an empty list.
     *     If tasks are found, it returns a list of tasks.
     * </p>
     * @return List of all tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        try {
            Optional<List<Task>> tasks = Optional.of(taskRepository.findAll(Sort.sort(Task.class)));
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

    /**
     * Get task by id
     * <p>
     *     This method is responsible for handling GET request to get a task by id.
     *     It delegates the request to the repository layer and returns the response to the controller.
     *     If any error occurs while getting the task, it returns an error response to the controller.
     *     If no task is found, it returns an error response to the controller.
     *     If task is found, it returns the task.
     * </p>
     * @param taskId Id of the task to be retrieved
     * @return      Task with the given id
     */
    @Transactional(readOnly = true)
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

    /**
     * Create task
     * <p>
     *     This method is responsible for handling POST request to create a task.
     *     It delegates the request to the repository layer and returns the response to the controller.
     *     If any error occurs while creating the task, it returns an error response to the controller.
     *     If task is created successfully, it returns the created task.
     * </p>
     * @param taskDto Task to be created
     * @return        Created task
     */
    @Transactional
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

    /**
     * Update task
     * <p>
     *     This method is responsible for handling PUT request to update a task.
     *     It delegates the request to the repository layer and returns the response to the controller.
     *     If any error occurs while updating the task, it returns an error response to the controller.
     *     If no task is found, it returns an error response to the controller.
     *     If task is updated successfully, it returns the updated task.
     * </p>
     * @param taskId  Id of the task to be updated
     * @param taskDto Task to be updated
     * @return        Updated task
     */
    @Transactional
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
    /**
     * Delete task
     * <p>
     *     This method is responsible for handling DELETE request to delete a task.
     *     It delegates the request to the repository layer and returns the response to the controller.
     *     If any error occurs while deleting the task, it returns an error response to the controller.
     *     If no task is found, it returns an error response to the controller.
     *     If task is deleted successfully, it returns a success response to the controller.
     * </p>
     * @param taskId Id of the task to be deleted
     */
    @Transactional
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
