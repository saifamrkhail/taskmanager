package com.craftworks.taskmanager.repository;

import com.craftworks.taskmanager.entity.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAll(Sort sort);

    Optional<Task> findById(Long id);

    <S extends Task> S save(S task);

    void deleteById(Long id);
}
