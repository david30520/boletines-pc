package com.example.tasks.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("No existe la tarea con id " + id);
    }
}
