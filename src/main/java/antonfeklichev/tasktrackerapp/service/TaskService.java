package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;

import java.util.List;

public interface TaskService {

    TaskDto addTask(NewTaskDto createTaskDto);

    TaskDto getTaskById(Long taskId);

    List<TaskDto> getAllTasks();

    TaskDto updateTaskById(Long taskId, TaskDto taskDto);

    void deleteTaskById(Long taskId); //TODO удалять подзадачи или кидать исключение


}
