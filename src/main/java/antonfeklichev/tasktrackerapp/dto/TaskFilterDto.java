package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record TaskFilterDto(TaskStatus status, String name) {
}
