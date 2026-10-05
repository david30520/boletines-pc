package com.example.tasks.dto;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    TaskPriority priority,
    LocalDate dueDate,
    Instant completedAt) {

  public static TaskResponse from(Task task) {
    return new TaskResponse(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getStatus(),
        task.getPriority(),
        task.getDueDate(),
        task.getCompletedAt());
  }
}
