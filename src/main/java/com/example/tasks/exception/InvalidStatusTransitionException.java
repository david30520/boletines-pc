package com.example.tasks.exception;

import com.example.tasks.domain.TaskStatus;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(TaskStatus from, TaskStatus to) {
    super("No se puede pasar de " + from + " a " + to);
  }
}
