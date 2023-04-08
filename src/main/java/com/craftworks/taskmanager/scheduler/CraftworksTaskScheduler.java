package com.craftworks.taskmanager.scheduler;

import com.craftworks.taskmanager.entity.Task;
import com.craftworks.taskmanager.enumeration.Priority;
import com.craftworks.taskmanager.enumeration.Status;
import com.craftworks.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class CraftworksTaskScheduler {

    private final Logger logger = LoggerFactory.getLogger(CraftworksTaskScheduler.class);

    private final TaskRepository taskRepository;

    @Autowired
    public CraftworksTaskScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private final Random random = new Random();

    /*@Scheduled(fixedDelay = 15000)
    public void createTask() {
        Task task = new Task();
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setDueDate(LocalDate.now().plusDays(random.nextInt(7)));
        task.setTitle("Sample task");
        task.setDescription("This is a sample task");
        task.setPriority(Priority.LOW);
        task.setStatus(Status.IN_PROGRESS);
        taskRepository.save(task);
        logger.info("Created task: {}", task);
    }*/
}
