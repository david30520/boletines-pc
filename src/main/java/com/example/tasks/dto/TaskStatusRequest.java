package com.example.tasks.dto;

import com.example.tasks.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(@NotNull(message = "El estado es obligatorio") TaskStatus status) {}
