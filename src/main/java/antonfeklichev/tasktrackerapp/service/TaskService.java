package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;

import java.util.List;

/**
 * Интерфейс определяет методы для создания, получения, обновления и удаления задач.
 *
 * Этот интерфейс предоставляет основу для бизнес-логики, связанной с управлением задачами,
 * и предполагает реализацию в классах, обрабатывающих логику операций над задачами.
 */
public interface TaskService {


    TaskDto addTask(NewTaskDto createTaskDto);

    TaskDto getTaskById(Long taskId);

    List<TaskDto> getTasksByFilter(QueryDslFilterDto filter);

    TaskDto updateTaskById(Long taskId, TaskDto taskDto);

    void deleteTaskById(Long taskId);


}
