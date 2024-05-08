package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record TaskDto(Long id, String name, String description, TaskStatus status) {
}
