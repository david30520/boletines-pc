package com.example.tasks.repository;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findAllByStatus(TaskStatus status, Sort sort);

  List<Task> findAllByPriority(TaskPriority priority, Sort sort);

  List<Task> findAllByStatusAndPriority(TaskStatus status, TaskPriority priority, Sort sort);

  Page<Task> findAllByStatus(TaskStatus status, Pageable pageable);

  Page<Task> findAllByPriority(TaskPriority priority, Pageable pageable);

  Page<Task> findAllByStatusAndPriority(
      TaskStatus status, TaskPriority priority, Pageable pageable);

  @Query(
      """
      select count(t) from Task t
      where (:status is null or t.status = :status)
        and (:priority is null or t.priority = :priority)
      """)
  long countMatching(@Param("status") TaskStatus status, @Param("priority") TaskPriority priority);

  // JPA limita setFirstResult a int; H2 admite un OFFSET mayor en SQL.
  @Query(
      value =
          """
      select * from tasks
      where (:status is null or status = :status)
        and (:priority is null or priority = :priority)
      order by id asc limit :size offset :offset
      """,
      nativeQuery = true)
  List<Task> findAtLargeOffset(
      @Param("status") String status,
      @Param("priority") String priority,
      @Param("size") int size,
      @Param("offset") long offset);
}
