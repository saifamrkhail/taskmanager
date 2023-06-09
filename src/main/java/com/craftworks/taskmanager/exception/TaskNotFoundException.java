package com.craftworks.taskmanager.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for when a task cannot be found.
 */
public class TaskNotFoundException extends RuntimeException {

    private HttpStatus status;

    public TaskNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
