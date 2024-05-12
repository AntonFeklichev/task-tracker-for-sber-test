package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.QSubTask;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.exception.SubTaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.mapper.SubTaskMapper;
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
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final SubTaskMapper subTaskMapper;
    private final TaskRepository taskRepository;

    @Override
    public SubTaskDto addSubTaskByTaskId(Long taskId, NewSubTaskDto newSubTaskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода SubTaskServiceImpl.addSubTaskByTaskId()" +
                              " не найдена задача по идентификатору {}.", taskId);
                    return new TaskNotFoundException("You cannot create SubTask with no Task");
                });
        SubTask subTask = subTaskMapper.toSubTask(newSubTaskDto);
        subTask.setTask(task);
        SubTask savedSubTask = subTaskRepository.save(subTask);

        return subTaskMapper.toSubTaskDto(savedSubTask);

    }

    @Override
    public SubTaskDto getSubTaskById(Long subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода SubTaskServiceImpl.getSubTaskById()" +
                              " не найдена подзадача по идентификатору {}.", subTaskId);
                    return new SubTaskNotFoundException("SubTask not found");
                });

        return subTaskMapper.toSubTaskDto(subTask);
    }

    @Override
    public List<SubTaskDto> getSubTasksByFilterAndTaskId(Long taskId, QueryDslFilterDto filter) {

        BooleanBuilder predicate = new BooleanBuilder(QSubTask.subTask.task.id.eq(taskId));

        if (filter.status() != null) {
            predicate.and(QSubTask.subTask.status.eq(filter.status()));
        }
        if (filter.name() != null && !filter.name().isBlank()) {
            predicate.and(QSubTask.subTask.name.containsIgnoreCase(filter.name()));
        }
        return Streamable.of(subTaskRepository.findAll(predicate))
                .map(subTaskMapper::toSubTaskDto)
                .toList();

    }

    @Override
    public SubTaskDto updateSubTaskById(Long subTaskId, SubTaskDto subTaskDto) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода SubTaskServiceImpl.updateSubTaskById()" +
                              " не найдена подзадача по идентификатору {}.", subTaskId);
                    return new SubTaskNotFoundException("SubTask not found");
                });
        subTaskMapper.patchSubTask(subTask, subTaskDto);
        SubTask savedSubTask = subTaskRepository.save(subTask);

        return subTaskMapper.toSubTaskDto(savedSubTask);
    }

    @Override
    public void deleteSubTaskById(Long subTaskId) {
        subTaskRepository.deleteById(subTaskId);
    }
}
