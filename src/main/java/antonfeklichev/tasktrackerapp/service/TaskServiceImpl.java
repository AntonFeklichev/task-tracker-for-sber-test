package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.QTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.mapper.TaskMapper;
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
    private final TaskMapper taskMapper;

    @Override
    public TaskDto addTask(NewTaskDto createTaskDto) {
        Task task = taskMapper.toTask(createTaskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(); //TODO Отработать Исключение

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
        Task task = taskRepository.findById(taskId).orElseThrow(); // TODO Отработать исключение
        taskMapper.patchTask(task, taskDto);
        Task savesTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savesTask);
    }


    @Override
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
