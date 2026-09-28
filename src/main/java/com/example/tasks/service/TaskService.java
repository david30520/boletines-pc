package com.example.tasks.service;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.TaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.repository.TaskRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

  private final TaskRepository repository;
  private final Validator validator;

  public TaskService(TaskRepository repository, Validator validator) {
    this.repository = repository;
    this.validator = validator;
  }

  public List<TaskResponse> findAll(TaskStatus status) {
    List<Task> tasks =
        status == null
            ? repository.findAll(Sort.by("id"))
            : repository.findAllByStatus(status, Sort.by("id"));
    return tasks.stream().map(TaskResponse::from).toList();
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
            request.dueDate());
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
        request.dueDate());
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

  private void validate(TaskRequest request) {
    // También protege las llamadas al servicio que no pasan por el controlador.
    var violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
