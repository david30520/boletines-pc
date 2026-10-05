package com.example.tasks.controller;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.dto.TaskStatusRequest;
import com.example.tasks.exception.GlobalExceptionHandler;
import com.example.tasks.exception.InvalidStatusTransitionException;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.service.TaskService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

  @Mock private TaskService service;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc =
        MockMvcBuilders.standaloneSetup(new TaskController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "URGENT", "high"})
  void invalidPriorityReturns400WithProblemDetail(String priority) throws Exception {
    mvc.perform(get("/api/tasks").param("priority", priority))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Prioridad no válida"))
        .andExpect(
            jsonPath("$.detail").value("El parámetro 'priority' debe ser LOW, MEDIUM o HIGH"));

    verifyNoInteractions(service);
  }

  @Test
  void unpagedListingPreservesArrayWithoutPaginationHeaders() throws Exception {
    when(service.findAll(null, null)).thenReturn(List.of(task(1), task(7)));

    mvc.perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[*].id", contains(1, 7)))
        .andExpect(header().doesNotExist("X-Page"))
        .andExpect(header().doesNotExist("X-Page-Size"))
        .andExpect(header().doesNotExist("X-Total-Count"))
        .andExpect(header().doesNotExist("X-Total-Pages"));

    verify(service).findAll(null, null);
    verifyNoMoreInteractions(service);
  }

  @Test
  void pagedListingPreservesArrayAndReturnsFilteredTotals() throws Exception {
    when(service.findPage(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, 1, 1))
        .thenReturn(new PageImpl<>(List.of(task(7)), PageRequest.of(1, 1), 3));

    mvc.perform(
            get("/api/tasks")
                .param("status", "IN_PROGRESS")
                .param("priority", "HIGH")
                .param("page", "1")
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(7))
        .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$[0].priority").value("HIGH"))
        .andExpect(header().string("X-Page", "1"))
        .andExpect(header().string("X-Page-Size", "1"))
        .andExpect(header().string("X-Total-Count", "3"))
        .andExpect(header().string("X-Total-Pages", "3"));

    verify(service).findPage(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, 1, 1);
    verifyNoMoreInteractions(service);
  }

  @ParameterizedTest
  @CsvSource({"page,1,1,20", "size,100,0,100"})
  void paginationDefaultsWhenOnlyOneParameterIsProvided(
      String parameter, String value, int expectedPage, int expectedSize) throws Exception {
    when(service.findPage(null, null, expectedPage, expectedSize))
        .thenReturn(Page.empty(PageRequest.of(expectedPage, expectedSize)));

    mvc.perform(get("/api/tasks").param(parameter, value))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"))
        .andExpect(header().string("X-Page", Integer.toString(expectedPage)))
        .andExpect(header().string("X-Page-Size", Integer.toString(expectedSize)))
        .andExpect(header().string("X-Total-Count", "0"))
        .andExpect(header().string("X-Total-Pages", "0"));

    verify(service).findPage(null, null, expectedPage, expectedSize);
    verifyNoMoreInteractions(service);
  }

  @Test
  void outOfRangePagePreservesRequestedIndexAndTotals() throws Exception {
    when(service.findPage(null, null, 3, 10))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(3, 10), 23));

    mvc.perform(get("/api/tasks").param("page", "3").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"))
        .andExpect(header().string("X-Page", "3"))
        .andExpect(header().string("X-Page-Size", "10"))
        .andExpect(header().string("X-Total-Count", "23"))
        .andExpect(header().string("X-Total-Pages", "3"));

    verify(service).findPage(null, null, 3, 10);
    verifyNoMoreInteractions(service);
  }

  @ParameterizedTest
  @CsvSource({"page,-1,0,2147483647", "size,101,1,100", "size,'',1,100"})
  void invalidPaginationReturns400WithoutCallingService(
      String parameter, String value, int minimum, int maximum) throws Exception {
    mvc.perform(get("/api/tasks").param(parameter, value))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Paginación no válida"))
        .andExpect(
            jsonPath("$.detail")
                .value(
                    "El parámetro '"
                        + parameter
                        + "' debe ser un número entero entre "
                        + minimum
                        + " y "
                        + maximum));

    verifyNoInteractions(service);
  }

  @Test
  void patchStatusReturnsUpdatedTask() throws Exception {
    Instant completedAt = Instant.parse("2026-09-21T12:00:00Z");
    TaskResponse done =
        new TaskResponse(
            7L,
            "Task 7",
            "Description",
            TaskStatus.DONE,
            TaskPriority.HIGH,
            LocalDate.of(2099, 12, 31),
            completedAt);
    when(service.changeStatus(7L, new TaskStatusRequest(TaskStatus.DONE))).thenReturn(done);

    mvc.perform(
            patch("/api/tasks/7/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DONE\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(7))
        .andExpect(jsonPath("$.status").value("DONE"))
        .andExpect(jsonPath("$.completedAt").exists());

    verify(service).changeStatus(7L, new TaskStatusRequest(TaskStatus.DONE));
  }

  @Test
  void patchStatusWithInvalidTransitionReturns409() throws Exception {
    when(service.changeStatus(7L, new TaskStatusRequest(TaskStatus.DONE)))
        .thenThrow(new InvalidStatusTransitionException(TaskStatus.TODO, TaskStatus.DONE));

    mvc.perform(
            patch("/api/tasks/7/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DONE\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.title").value("Transición no válida"))
        .andExpect(jsonPath("$.detail").value("No se puede pasar de TODO a DONE"));
  }

  @Test
  void patchStatusOfMissingTaskReturns404() throws Exception {
    when(service.changeStatus(99L, new TaskStatusRequest(TaskStatus.IN_PROGRESS)))
        .thenThrow(new TaskNotFoundException(99L));

    mvc.perform(
            patch("/api/tasks/99/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_PROGRESS\"}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Tarea no encontrada"));
  }

  @Test
  void patchStatusWithoutStatusReturns400() throws Exception {
    mvc.perform(patch("/api/tasks/7/status").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Datos no válidos"))
        .andExpect(jsonPath("$.errors.status").value("El estado es obligatorio"));

    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @ValueSource(strings = {"{\"status\":\"FINISHED\"}", "{\"status\":\"done\"}", "no es json"})
  void patchStatusWithUnreadableBodyReturns400(String body) throws Exception {
    mvc.perform(patch("/api/tasks/7/status").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("JSON no válido"));

    verifyNoInteractions(service);
  }

  private TaskResponse task(int id) {
    return new TaskResponse(
        (long) id,
        "Task " + id,
        "Description",
        TaskStatus.IN_PROGRESS,
        TaskPriority.HIGH,
        LocalDate.of(2099, 12, 31),
        null);
  }
}
