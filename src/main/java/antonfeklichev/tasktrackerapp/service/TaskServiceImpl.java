package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskFilterDto;
import antonfeklichev.tasktrackerapp.entity.QTask;
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
        return null;
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        return null;
    }

    @Override
    public List<TaskDto> getAllTasks(TaskFilterDto filter) {
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
        return null;
    }

    @Override
    public void deleteTaskById(Long taskId) {

    }
}
