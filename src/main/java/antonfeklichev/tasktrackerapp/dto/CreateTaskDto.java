package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record CreateTaskDto(String name, String description, TaskStatus status) {
}
