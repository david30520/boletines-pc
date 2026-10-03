package com.example.tasks.controller;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.TaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.exception.InvalidPaginationException;
import com.example.tasks.exception.InvalidPriorityException;
import com.example.tasks.service.TaskService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
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
  public ResponseEntity<List<TaskResponse>> findAll(
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) String priority,
      @RequestParam(required = false) String page,
      @RequestParam(required = false) String size) {

    TaskPriority parsedPriority = null;
    if (priority != null) {
      try {
        parsedPriority = TaskPriority.valueOf(priority);
      } catch (IllegalArgumentException exception) {
        throw new InvalidPriorityException();
      }
    }

    if (page == null && size == null) {
      return ResponseEntity.ok(service.findAll(status, parsedPriority));
    }

    int pageNumber = parsePaginationParameter(page, "page", 0, 0, Integer.MAX_VALUE);
    int pageSize = parsePaginationParameter(size, "size", 20, 1, 100);
    Page<TaskResponse> result = service.findPage(status, parsedPriority, pageNumber, pageSize);
    // Page.getTotalPages() devuelve int y puede truncar totales muy grandes.
    long totalPages =
        result.getTotalElements() / result.getSize()
            + (result.getTotalElements() % result.getSize() == 0 ? 0 : 1);
    return ResponseEntity.ok()
        .header("X-Page", Integer.toString(result.getNumber()))
        .header("X-Page-Size", Integer.toString(result.getSize()))
        .header("X-Total-Count", Long.toString(result.getTotalElements()))
        .header("X-Total-Pages", Long.toString(totalPages))
        .body(result.getContent());
  }

  private int parsePaginationParameter(
      String value, String parameter, int defaultValue, int minimum, int maximum) {
    if (value == null) {
      return defaultValue;
    }
    int parsed;
    try {
      parsed = Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      throw new InvalidPaginationException(parameter, minimum, maximum);
    }
    if (parsed < minimum || parsed > maximum) {
      throw new InvalidPaginationException(parameter, minimum, maximum);
    }
    return parsed;
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
