package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.QTask;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.DeleteTaskException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.UpdateTaskException;
import antonfeklichev.tasktrackerapp.mapper.TaskMapper;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskMapper taskMapper;


    @Override
    public TaskDto addTask(NewTaskDto createTaskDto) {
        Task task = taskMapper.toTask(createTaskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода TaskServiceImpl.getTaskById() не найдена задача по идентификатору {}.", taskId);
                    return new TaskNotFoundException("Task not found");
                });

        return taskMapper.toTaskDto(task);
    }

    @Override
    public List<TaskDto> getTasksByFilter(QueryDslFilterDto filter) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (filter.status() != null) {
            predicate.and(QTask.task.status.eq(filter.status()));
        }
        if (filter.name() != null && !filter.name().isBlank()) {
            predicate.and(QTask.task.name.containsIgnoreCase(filter.name()));
        }

        return Streamable.of(taskRepository.findAll(predicate))
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Override
    public TaskDto updateTaskById(Long taskId, TaskDto taskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода TaskServiceImpl.updateTaskById() не найдена задача по идентификатору {}.", taskId);
                    return new TaskNotFoundException("Task not found");
                });

        List<SubTask> subTaskList = subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE);

        if (!subTaskList.isEmpty() && taskDto.status().equals(TaskStatus.DONE)) {
            log.error("При вызове метода TaskServiceImpl.updateTaskById() статус задачи по идентификатору {} не может быть изменен на DONE. " +
                      "Вначале поменяйте статус связанных подзадач на DONE или удалите подзадачи.", taskId);
            throw new UpdateTaskException("You cannot set DONE status to Task, while its SubTasks in progress.");
        }
        taskMapper.patchTask(task, taskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    @Override
    public void deleteTaskById(Long taskId) {

        List<SubTask> subTaskList = subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE);

        if (!subTaskList.isEmpty()) {
            log.error("При вызове метода TaskServiceImpl.deleteTaskById() задача по идентификатору {} не может быть удалена. " +
                      "Вначале поменяйте статус связанных подзадач на DONE или удалите подзадачи.", taskId);
            throw new DeleteTaskException("Delete active SubTasks of this Task first");
        }
        taskRepository.deleteById(taskId);
    }
}

