package com.craftworks.taskmanager.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Future;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import com.craftworks.taskmanager.enumeration.Priority;
import com.craftworks.taskmanager.enumeration.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateTaskDto {
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
