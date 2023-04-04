package com.craftworks.taskmanager.service;

import com.craftworks.taskmanager.entity.Task;
import com.craftworks.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "dueDate"));
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(Long id, Task taskToUpdate) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskToUpdate.getTitle());
            task.setDescription(taskToUpdate.getDescription());
            task.setDueDate(taskToUpdate.getDueDate());
            task.setPriority(taskToUpdate.getPriority());
            task.setStatus(taskToUpdate.getStatus());
            task.setUpdatedAt(taskToUpdate.getUpdatedAt());
            return Optional.of(taskRepository.save(task));
        } else {
            return Optional.empty();
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
