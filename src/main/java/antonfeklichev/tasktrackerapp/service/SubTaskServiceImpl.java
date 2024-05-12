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

/**
 * Класс <code>SubTaskServiceImpl</code> реализует интерфейс {@link SubTaskService} и предоставляет методы для работы с подзадачами.
 * Основная функциональность включает создание, чтение, обновление и удаление (CRUD) подзадач.
 * <p>
 * Поля класса:
 * <ul>
 *   <li><b>subTaskRepository</b> - репозиторий для получения подзадач из базы данных.</li>
 *   <li><b>subTaskMapper</b> - маппер для преобразования подзадач в DTO и обратно.</li>
 *   <li><b>taskRepository</b> - репозиторий для получения основных задач из базы данных.</li>
 * </ul>
 * <p>
 * Этот сервис служит связующим звеном между базой данных и клиентским приложением, обеспечивая необходимую бизнес-логику для обработки запросов на подзадачи.
 *
 * @see SubTaskService
 * @see SubTask
 * @see Task
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final SubTaskMapper subTaskMapper;
    private final TaskRepository taskRepository;


    /**
     * Добавляет новую подзадачу для указанной задачи.
     *
     * @param taskId Идентификатор задачи, к которой нужно добавить подзадачу.
     * @param newSubTaskDto DTO с данными для создания новой подзадачи.
     * @return DTO созданной подзадачи.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */

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

    /**
     * Возвращает подзадачу по её идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи.
     * @return DTO запрашиваемой подзадачи.
     * @throws SubTaskNotFoundException если подзадача с указанным идентификатором не найдена.
     */
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

    /**
     * Возвращает список подзадач по фильтрам и идентификатору задачи.
     *
     * @param taskId Идентификатор задачи.
     * @param filter DTO, содержащий параметры фильтрации для поиска подзадач.
     * @return Список DTO подзадач, соответствующих заданным критериям.
     */
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

    /**
     * Обновляет подзадачу по её идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи для обновления.
     * @param subTaskDto DTO с обновленной информацией для подзадачи.
     * @return Обновленное DTO подзадачи.
     * @throws SubTaskNotFoundException если подзадача с указанным идентификатором не найдена.
     */
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

    /**
     * Удаляет подзадачу по идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи для удаления.
     */
    @Override
    public void deleteSubTaskById(Long subTaskId) {
        subTaskRepository.deleteById(subTaskId);
    }
}
