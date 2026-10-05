package com.example.tasks.service;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.TaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.dto.TaskStatusRequest;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.repository.TaskRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

  private final TaskRepository repository;
  private final Validator validator;
  private final Clock clock;

  public TaskService(TaskRepository repository, Validator validator, Clock clock) {
    this.repository = repository;
    this.validator = validator;
    this.clock = clock;
  }

  public List<TaskResponse> findAll(TaskStatus status, TaskPriority priority) {
    Sort sort = Sort.by("id");
    List<Task> tasks;

    if (status == null && priority == null) {
      tasks = repository.findAll(sort);
    } else if (priority == null) {
      tasks = repository.findAllByStatus(status, sort);
    } else if (status == null) {
      tasks = repository.findAllByPriority(priority, sort);
    } else {
      tasks = repository.findAllByStatusAndPriority(status, priority, sort);
    }

    return tasks.stream().map(TaskResponse::from).toList();
  }

  public Page<TaskResponse> findPage(TaskStatus status, TaskPriority priority, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
    Page<Task> tasks;

    if (status == null && priority == null) {
      tasks = repository.findAll(pageable);
    } else if (priority == null) {
      tasks = repository.findAllByStatus(status, pageable);
    } else if (status == null) {
      tasks = repository.findAllByPriority(priority, pageable);
    } else {
      tasks = repository.findAllByStatusAndPriority(status, priority, pageable);
    }

    return tasks.map(TaskResponse::from);
  }

  public TaskResponse findById(Long id) {
    return TaskResponse.from(findTask(id));
  }

  @Transactional
  public TaskResponse create(TaskRequest request) {
    validate(request);
    Task task =
        new Task(
            request.title().strip(),
            request.description(),
            request.status(),
            request.priority(),
            request.dueDate(),
            now());
    return TaskResponse.from(repository.save(task));
  }

  @Transactional
  public TaskResponse update(Long id, TaskRequest request) {
    Task task = findTask(id);
    validate(request);
    task.update(
        request.title().strip(),
        request.description(),
        request.status(),
        request.priority(),
        request.dueDate(),
        now());
    return TaskResponse.from(repository.save(task));
  }

  @Transactional
  public TaskResponse changeStatus(Long id, TaskStatusRequest request) {
    Task task = findTask(id);
    validate(request);
    // Solo cambia el estado: no se revalida la fecha límite, así una tarea vencida puede cerrarse.
    task.changeStatus(request.status(), now());
    return TaskResponse.from(repository.save(task));
  }

  @Transactional
  public void delete(Long id) {
    Task task = findTask(id);
    repository.delete(task);
  }

  private Task findTask(Long id) {
    return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
  }

  private Instant now() {
    // Sin fracciones: la base de datos no conserva toda la precisión y la respuesta variaría.
    return clock.instant().truncatedTo(ChronoUnit.SECONDS);
  }

  private void validate(Object request) {
    // También protege las llamadas al servicio que no pasan por el controlador.
    var violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
