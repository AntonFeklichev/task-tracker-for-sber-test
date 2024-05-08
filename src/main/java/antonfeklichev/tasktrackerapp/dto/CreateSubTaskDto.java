package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;

public record CreateSubTaskDto(String name, String description, TaskStatus status, Long taskId) {
} //TODO Разобрать с Матвеем taskId
