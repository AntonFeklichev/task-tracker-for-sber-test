package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер обеспечивает обработку HTTP запросов для операций CRUD по задачам.
 * <p>
 * Этот контроллер взаимодействует с {@link TaskService} для выполнения бизнес-логики и
 * возвращает данные в формате RESTful HTTP ответов.
 */
@RestController
@RequestMapping(path = "api/v1/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    /**
     * Создает новую задачу на основе данных, предоставленных в теле запроса.
     *
     * @param newTaskDto DTO содержащее данные для создания задачи.
     * @return ResponseEntity содержащий созданную задачу.
     */
    @PostMapping
    public ResponseEntity<TaskDto> addTask(@RequestBody NewTaskDto newTaskDto) {
        log.info("Получен запрос на создание задачи {}", newTaskDto);
        return ResponseEntity.ok()
                .body(taskService.addTask(newTaskDto));
    }

    /**
     * Возвращает задачу по указанному идентификатору.
     *
     * @param taskId Идентификатор задачи.
     * @return ResponseEntity содержащий DTO задачи.
     */
    @GetMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "taskId") Long taskId) {
        return ResponseEntity.ok().body(taskService.getTaskById(taskId));
    }

    /**
     * Возвращает список задач, соответствующих заданным фильтрам.
     *
     * @param filter Фильтры для выборки задач.
     * @return ResponseEntity содержащий список DTO задач.
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByFilter(@RequestBody QueryDslFilterDto filter) {
        return ResponseEntity.ok()
                .body(taskService.getTasksByFilter(filter));
    }

    /**
     * Обновляет задачу по указанному идентификатору на основе данных, предоставленных в теле запроса.
     *
     * @param taskId Идентификатор задачи для обновления.
     * @param taskDto DTO с обновленной информацией о задаче.
     * @return ResponseEntity содержащий обновленную задачу.
     */
    @PatchMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> updateTaskById(@PathVariable(name = "taskId")
                                                  Long taskId,
                                                  @RequestBody
                                                  TaskDto taskDto) {
        return ResponseEntity.ok()
                .body(taskService.updateTaskById(taskId, taskDto));

    }

    /**
     * Удаляет задачу по указанному идентификатору.
     *
     * @param taskId Идентификатор задачи для удаления.
     * @return ResponseEntity с HTTP статусом No Content, указывающим на успешное удаление.
     */
    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable(name = "taskId") Long taskId) {

        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }


}
