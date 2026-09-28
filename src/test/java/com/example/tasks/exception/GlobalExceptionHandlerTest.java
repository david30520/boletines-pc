package com.example.tasks.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.tasks.controller.TaskController;
import com.example.tasks.dto.TaskRequest;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private ServletWebRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/tasks/99"));
  }

  @Test
  void missingTaskReturns404WithHelpfulDetail() {
    ResponseEntity<Object> response =
        handler.handleTaskNotFound(new TaskNotFoundException(99L), request);

    ProblemDetail body = assertInstanceOf(ProblemDetail.class, response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(404, body.getStatus());
    assertEquals("Tarea no encontrada", body.getTitle());
    assertEquals("No existe la tarea con id 99", body.getDetail());
    assertFalse(body.toString().contains("stackTrace"));
  }

  @Test
  void invalidFieldsReturn400WithErrorsByField() throws NoSuchMethodException {
    var binding = new BeanPropertyBindingResult(null, "taskRequest");
    binding.addError(new FieldError("taskRequest", "title", "El título es obligatorio"));
    Method method = TaskController.class.getMethod("create", TaskRequest.class);
    var exception = new MethodArgumentNotValidException(new MethodParameter(method, 0), binding);

    ResponseEntity<Object> response =
        handler.handleMethodArgumentNotValid(
            exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    ProblemDetail body = assertInstanceOf(ProblemDetail.class, response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Map.of("title", "El título es obligatorio"), body.getProperties().get("errors"));
  }

  @Test
  void unreadableJsonReturns400WithoutExposingParserInternals() {
    var exception =
        new HttpMessageNotReadableException(
            "Detalles internos del parser", new MockHttpInputMessage(new byte[0]));

    ResponseEntity<Object> response =
        handler.handleHttpMessageNotReadable(
            exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    ProblemDetail body = assertInstanceOf(ProblemDetail.class, response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("JSON no válido", body.getTitle());
    assertFalse(body.getDetail().contains("Detalles internos"));
  }

  @Test
  void nonNumericIdReturns400() {
    var exception = new TypeMismatchException("abc", Long.class);

    ResponseEntity<Object> response =
        handler.handleTypeMismatch(exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    ProblemDetail body = assertInstanceOf(ProblemDetail.class, response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("El identificador de la tarea debe ser un número entero válido", body.getDetail());
  }
}
