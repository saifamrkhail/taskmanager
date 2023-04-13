package com.craftworks.taskmanager.dto;


import com.craftworks.taskmanager.enumeration.Priority;
import com.craftworks.taskmanager.enumeration.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Data Transfer Object for a task.
 */
@Data
public class TaskDto {

    @NotNull
    private Long id;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Future
    private LocalDate dueDate;

    @Future
    private LocalDateTime resolvedAt;

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @Size(min = 1, max = 1000)
    private String description;

    @NotNull
    public Priority priority;

    @NotNull
    public Status status;
}
