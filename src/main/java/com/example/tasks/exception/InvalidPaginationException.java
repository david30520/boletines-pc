package com.example.tasks.exception;

public class InvalidPaginationException extends RuntimeException {

  public InvalidPaginationException(String parameter, int minimum, int maximum) {
    super(
        "El parámetro '"
            + parameter
            + "' debe ser un número entero entre "
            + minimum
            + " y "
            + maximum);
  }
}
