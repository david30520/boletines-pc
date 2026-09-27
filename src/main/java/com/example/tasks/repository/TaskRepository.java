package com.example.tasks.repository;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskStatus;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStatus(TaskStatus status, Sort sort);
}
