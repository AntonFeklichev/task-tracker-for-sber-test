package antonfeklichev.tasktrackerapp.dto;

import antonfeklichev.tasktrackerapp.entity.TaskStatus;


public record QueryDslFilterDto(TaskStatus status, String name) {
}
