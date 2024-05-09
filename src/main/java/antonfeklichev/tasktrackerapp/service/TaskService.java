package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskFilterDto;

import java.util.List;

public interface TaskService {

    TaskDto addTask(NewTaskDto createTaskDto);

    TaskDto getTaskById(Long taskId);

    List<TaskDto> getAllTasks(TaskFilterDto filter);

    TaskDto updateTaskById(Long taskId, TaskDto taskDto);

    void deleteTaskById(Long taskId);


}
