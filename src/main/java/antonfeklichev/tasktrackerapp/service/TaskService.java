package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;

import java.util.List;

public interface TaskService {

    TaskDto addTask(NewTaskDto createTaskDto);

    List<TaskDto> getAllTasks();

    TaskDto updateTaskStatusByTaskId(Long taskId, TaskDto taskDto);

    void deleteTaskById(Long taskId);
}
