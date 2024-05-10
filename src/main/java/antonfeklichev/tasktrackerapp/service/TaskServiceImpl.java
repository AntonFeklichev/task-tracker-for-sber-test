package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.QSubTask;
import antonfeklichev.tasktrackerapp.entity.QTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.DeleteTaskException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.UpdateTaskException;
import antonfeklichev.tasktrackerapp.mapper.SubTaskMapper;
import antonfeklichev.tasktrackerapp.mapper.TaskMapper;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskMapper taskMapper;
    private final SubTaskMapper subTaskMapper;

    @Override
    public TaskDto addTask(NewTaskDto createTaskDto) {
        Task task = taskMapper.toTask(createTaskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

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
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        BooleanBuilder predicate = new BooleanBuilder(QSubTask.subTask.task.id.eq(taskId));
        predicate.and(QSubTask.subTask.status.ne(TaskStatus.DONE));

        List<SubTaskDto> list = Streamable.of(subTaskRepository.findAll(predicate))
                .map(subTaskMapper::toSubTaskDto)
                .toList();

        if(!list.isEmpty() && taskDto.status().equals(TaskStatus.DONE)) {
            throw new UpdateTaskException("You cannot set DONE status to Task, while its SubTasks in progress.");
        } // TODO проверить метод

        taskMapper.patchTask(task, taskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }


    @Override
    public void deleteTaskById(Long taskId) {

        BooleanBuilder predicate = new BooleanBuilder(QSubTask.subTask.task.id.eq(taskId));
        predicate.and(QSubTask.subTask.status.ne(TaskStatus.DONE));

        List<SubTaskDto> list = Streamable.of(subTaskRepository.findAll(predicate))
                .map(subTaskMapper::toSubTaskDto)
                .toList();

        if (!list.isEmpty()) {
            throw new DeleteTaskException("Delete active SubTasks of this Task first");
        } // TODO Сделать ExceptionHandler

        taskRepository.deleteById(taskId);
    }
}

