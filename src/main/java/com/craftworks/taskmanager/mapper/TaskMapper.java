package com.craftworks.taskmanager.mapper;

import com.craftworks.taskmanager.dto.CreateTaskDto;
import com.craftworks.taskmanager.dto.UpdateTaskDto;
import com.craftworks.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    UpdateTaskDto toUpdateDto(Task task);

    Task createTaskDtoToEntity(CreateTaskDto taskDto, @MappingTarget Task task);
    Task updateTaskDtoToEntity(UpdateTaskDto taskDto, @MappingTarget Task task);
}
