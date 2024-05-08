package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record NewTaskDto(String name, String description, TaskStatus status) {
}
