package com.example.tasks.exception;

public class InvalidPriorityException extends RuntimeException {

  public InvalidPriorityException() {
    super("El parámetro 'priority' debe ser LOW, MEDIUM o HIGH");
  }
}
