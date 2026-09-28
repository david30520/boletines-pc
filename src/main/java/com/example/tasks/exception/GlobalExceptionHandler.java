package com.example.tasks.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Object> handleTaskNotFound(
      TaskNotFoundException exception, WebRequest request) {
    ProblemDetail problem =
        problem(HttpStatus.NOT_FOUND, "Tarea no encontrada", exception.getMessage());
    return handleExceptionInternal(
        exception, problem, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException exception, WebRequest request) {
    Map<String, String> errors = new TreeMap<>();
    exception
        .getConstraintViolations()
        .forEach(
            violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
    return handleExceptionInternal(
        exception, validationProblem(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> errors = new TreeMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
    return handleExceptionInternal(exception, validationProblem(errors), headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problem =
        problem(
            HttpStatus.BAD_REQUEST,
            "JSON no válido",
            "Comprueba el cuerpo JSON, los estados, las prioridades y las fechas (AAAA-MM-DD)");
    return handleExceptionInternal(exception, problem, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problem =
        problem(
            HttpStatus.BAD_REQUEST,
            "Parámetro no válido",
            "El identificador de la tarea debe ser un número entero válido");
    return handleExceptionInternal(exception, problem, headers, status, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnexpectedException(Exception exception, WebRequest request) {
    LOG.error("Error inesperado al procesar la petición", exception);
    ProblemDetail problem =
        problem(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "No se pudo completar la operación");
    return handleExceptionInternal(
        exception, problem, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  private ProblemDetail validationProblem(Map<String, String> errors) {
    ProblemDetail problem =
        problem(
            HttpStatus.BAD_REQUEST,
            "Datos no válidos",
            "Uno o varios campos no cumplen las restricciones");
    problem.setProperty("errors", errors);
    return problem;
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    return problem;
  }
}
