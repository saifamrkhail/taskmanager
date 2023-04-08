package com.craftworks.taskmanager.mapper;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.TaskDto;
import com.craftworks.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "createdAt", qualifiedByName = "setCreatedAt")
    Task createTaskDtoToEntity(CreateTaskDto taskDto, @MappingTarget Task task);

    @Named("setCreatedAt")
    default LocalDateTime setCreatedAt(LocalDateTime createdAt) {
        return LocalDateTime.now();
    }

    @Mapping(target = "updatedAt", qualifiedByName = "setUpdatedAt")
    Task taskDtoToEntity(TaskDto taskDto, @MappingTarget Task task);

    @Named("setUpdatedAt")
    default LocalDateTime setUpdatedAt(LocalDateTime updatedAt) {
        return LocalDateTime.now();
    }

    TaskDto taskToTaskDto(Task task);
    List<TaskDto> taskListToTaskDtoList(List<Task> taskList);
}
