package com.example.tasks.domain;

import com.example.tasks.exception.InvalidStatusTransitionException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El título es obligatorio")
  @Size(max = 120, message = "El título no puede superar 120 caracteres")
  @Column(nullable = false, length = 120)
  private String title;

  @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
  @Column(length = 2000)
  private String description;

  @NotNull(message = "El estado es obligatorio")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TaskStatus status;

  @NotNull(message = "La prioridad es obligatoria")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TaskPriority priority;

  // La regla temporal se valida al recibir cambios: una tarea guardada puede vencer.
  @NotNull(message = "La fecha límite es obligatoria")
  @Column(nullable = false)
  private LocalDate dueDate;

  // Solo tiene valor mientras la tarea está en DONE.
  private Instant completedAt;

  protected Task() {
    // Constructor requerido por JPA.
  }

  public Task(
      String title,
      String description,
      TaskStatus status,
      TaskPriority priority,
      LocalDate dueDate,
      Instant now) {
    // Al crear se admite cualquier estado inicial; las reglas de transición aplican a los cambios.
    this.status = status;
    this.completedAt = status == TaskStatus.DONE ? now : null;
    assignFields(title, description, priority, dueDate);
  }

  public void update(
      String title,
      String description,
      TaskStatus status,
      TaskPriority priority,
      LocalDate dueDate,
      Instant now) {
    // Primero el estado: si la transición no es válida, la entidad queda intacta.
    changeStatus(status, now);
    assignFields(title, description, priority, dueDate);
  }

  public void changeStatus(TaskStatus target, Instant now) {
    if (!status.canTransitionTo(target)) {
      throw new InvalidStatusTransitionException(status, target);
    }
    if (status == target) {
      return;
    }
    status = target;
    completedAt = target == TaskStatus.DONE ? now : null;
  }

  private void assignFields(
      String title, String description, TaskPriority priority, LocalDate dueDate) {
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.dueDate = dueDate;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }
}
