package com.craftworks.taskmanager.repository;

import com.craftworks.taskmanager.entity.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @NonNull
    List<Task> findAll(@NonNull Sort sort);
    @NonNull
    Optional<Task> findById(@NonNull Long id);
    @NonNull
    <S extends Task> S save(@NonNull S task);

    void deleteById(@NonNull Long id);
}
