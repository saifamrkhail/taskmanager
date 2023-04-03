package com.craftworks.taskmanager.repository;

import com.craftworks.taskmanager.entity.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    /**
     * Find all tasks ordered by created date
     * @return
     */
    List<Task> findAll(Task task, Sort sort);

    /**
     * Find task by id
     * @param id
     * @return
     */
    Optional<Task> findById(UUID id);

    Task create(Task task);

    /**
     * Update task by id
     * @param id
     * @return
     */
    Task save(Task task);

    /**
     * Delete task by id
     * @param id
     */
    void deleteById(UUID id);
}
