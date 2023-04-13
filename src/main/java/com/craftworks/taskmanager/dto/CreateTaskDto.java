package com.craftworks.taskmanager.dto;

import com.craftworks.taskmanager.enumeration.Priority;
import com.craftworks.taskmanager.enumeration.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a new task.
 */
@Data
public class CreateTaskDto {

    private LocalDateTime createdAt;
    private LocalDate dueDate;

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @Size(min = 1, max = 1000)
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Status status;
}
