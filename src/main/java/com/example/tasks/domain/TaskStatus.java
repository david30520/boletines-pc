package com.example.tasks.domain;

public enum TaskStatus {
  TODO,
  IN_PROGRESS,
  DONE;

  /** Indica si se permite pasar a {@code target}; mantener el mismo estado siempre se permite. */
  public boolean canTransitionTo(TaskStatus target) {
    if (this == target) {
      return true;
    }
    return switch (this) {
      case TODO -> target == IN_PROGRESS;
      case IN_PROGRESS -> target == TODO || target == DONE;
      case DONE -> target == IN_PROGRESS;
    };
  }
}
