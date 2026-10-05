package com.example.tasks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TaskStatusTest {

  @ParameterizedTest(name = "{0} -> {1}: {2}")
  @CsvSource({
    "TODO, TODO, true",
    "TODO, IN_PROGRESS, true",
    "TODO, DONE, false",
    "IN_PROGRESS, TODO, true",
    "IN_PROGRESS, IN_PROGRESS, true",
    "IN_PROGRESS, DONE, true",
    "DONE, TODO, false",
    "DONE, IN_PROGRESS, true",
    "DONE, DONE, true"
  })
  void transitionsFollowTheWorkflow(TaskStatus from, TaskStatus to, boolean allowed) {
    assertEquals(allowed, from.canTransitionTo(to));
  }
}
