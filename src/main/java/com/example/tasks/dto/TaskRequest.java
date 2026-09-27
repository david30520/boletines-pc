package com.example.tasks.dto;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120, message = "El título no puede superar 120 caracteres")
        String title,

        @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
        String description,

        @NotNull(message = "El estado es obligatorio")
        TaskStatus status,

        @NotNull(message = "La prioridad es obligatoria")
        TaskPriority priority,

        @NotNull(message = "La fecha límite es obligatoria")
        @FutureOrPresent(message = "La fecha límite no puede estar en el pasado")
        LocalDate dueDate) {
}
