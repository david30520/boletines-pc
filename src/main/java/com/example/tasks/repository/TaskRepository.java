package com.example.tasks.repository;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findAllByStatus(TaskStatus status, Sort sort);

  List<Task> findAllByPriority(TaskPriority priority, Sort sort);

  List<Task> findAllByStatusAndPriority(TaskStatus status, TaskPriority priority, Sort sort);
}
