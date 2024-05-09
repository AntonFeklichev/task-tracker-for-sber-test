package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record SubTaskDto(Long id, String name, String description, TaskStatus status, Long taskId) {
}
