package com.craftworks.taskmanager.controller;

import com.craftworks.taskmanager.entity.Task;
import com.craftworks.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody Task updatedTask) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            Task savedTask = task.get();
            savedTask.setTitle(updatedTask.getTitle());
            savedTask.setDescription(updatedTask.getDescription());
            savedTask.setDueDate(updatedTask.getDueDate());
            savedTask.setPriority(updatedTask.getPriority());
            savedTask.setStatus(updatedTask.getStatus());
            savedTask.setResolvedAt(updatedTask.getResolvedAt());
            taskRepository.save(savedTask);
            return ResponseEntity.ok(savedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}