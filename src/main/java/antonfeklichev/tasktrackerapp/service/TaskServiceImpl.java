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

/**
 * Сервис для управления задачами.
 * Класс <code>TaskServiceImpl</code> реализует интерфейс {@link TaskService} и предоставляет методы для создания, получения,
 * обновления и удаления задач.
 * <p>
 * Поля класса:
 * <ul>
 *   <li><b>taskRepository</b> - репозиторий для доступа и управления задачами в базе данных.</li>
 *   <li><b>subTaskRepository</b> - репозиторий для доступа к подзадачам, связанным с основными задачами.</li>
 *   <li><b>taskMapper</b> - маппер для конвертации между {@link TaskDto}, {@link NewTaskDto} и {@link Task} сущностями.</li>
 * </ul>
 * <p>
 *
 * @see TaskService
 * @see Task
 * @see TaskDto
 * @see SubTask
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskMapper taskMapper;

    /**
     * Добавляет новую задачу в систему.
     *
     * @param createTaskDto DTO с данными для создания новой задачи.
     * @return DTO созданной задачи.
     */
    @Override
    public TaskDto addTask(NewTaskDto createTaskDto) {
        Task task = taskMapper.toTask(createTaskDto);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    /**
     * Возвращает задачу по её идентификатору.
     *
     * @param taskId идентификатор задачи.
     * @return DTO запрашиваемой задачи.
     * @throws TaskNotFoundException если задача не найдена.
     */
    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("При вызове метода TaskServiceImpl.getTaskById() не найдена задача по идентификатору {}.", taskId);
                    return new TaskNotFoundException("Task not found");
                });

        return taskMapper.toTaskDto(task);
    }

    /**
     * Возвращает список всех задач, соответствующих заданным критериям фильтрации.
     *
     * @param filter DTO критерии фильтрации задач.
     * @return список задач, удовлетворяющих критериям фильтра.
     */
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

    /**
     * Обновляет задачу по её идентификатору.
     *
     * @param taskId Идентификатор задачи для обновления.
     * @param taskDto DTO с обновленной информацией задачи.
     * @return обновленное DTO задачи.
     * @throws UpdateTaskException если изменение статуса на 'DONE' невозможно из-за незавершенных подзадач.
     */
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

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param taskId идентификатор задачи для удаления.
     * @throws DeleteTaskException если задачу невозможно удалить из-за наличия активных подзадач.
     */
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

