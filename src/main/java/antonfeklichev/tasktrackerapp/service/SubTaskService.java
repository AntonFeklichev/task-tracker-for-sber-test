package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;

import java.util.List;

/**
 * Интерфейс определяет методы для создания, получения, обновления и удаления подзадач,
 * связанных с определенной задачей.
 *
 * Этот интерфейс служит основой для реализации бизнес-логики управления подзадачами
 * и предполагает реализацию в классах, обрабатывающих конкретные операции с подзадачами.
 */
public interface SubTaskService {
    SubTaskDto addSubTaskByTaskId(Long taskId, NewSubTaskDto newSubTaskDto);

    SubTaskDto getSubTaskById(Long subTaskId);

    List<SubTaskDto> getSubTasksByFilterAndTaskId(Long taskId, QueryDslFilterDto filter);

    SubTaskDto updateSubTaskById(Long subTaskId, SubTaskDto subTaskDto);

    void deleteSubTaskById(Long subTaskId);
}
