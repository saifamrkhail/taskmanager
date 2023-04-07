package com.craftworks.taskmanager.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

public class TaskAccessException extends DataAccessException {
    private HttpStatus status;
    public TaskAccessException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
