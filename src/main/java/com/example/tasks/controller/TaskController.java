package com.example.tasks.controller;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.TaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.exception.InvalidPriorityException;
import com.example.tasks.service.TaskService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Controlador REST encargado de gestionar las operaciones sobre tareas. */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService service;

  public TaskController(TaskService service) {
    this.service = service;
  }

  @GetMapping
  public List<TaskResponse> findAll(
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) String priority) {

    TaskPriority parsedPriority = null;
    if (priority != null) {
      try {
        parsedPriority = TaskPriority.valueOf(priority);
      } catch (IllegalArgumentException exception) {
        throw new InvalidPriorityException();
      }
    }

    return service.findAll(status, parsedPriority);
  }

  @GetMapping("/{id}")
  public TaskResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
    TaskResponse task = service.create(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(task.id())
            .toUri();
    return ResponseEntity.created(location).body(task);
  }

  @PutMapping("/{id}")
  public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
