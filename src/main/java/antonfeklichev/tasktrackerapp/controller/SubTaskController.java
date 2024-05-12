package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер предоставляет REST API для создания, получения, обновления и удаления подзадач,
 * связанных с конкретными задачами.
 * <p>
 * Этот контроллер использует {@link SubTaskService} для выполнения бизнес-логики и
 * возвращает данные в виде RESTful ответов.
 */
@RestController
@RequestMapping(path = "/api/v1/subtasks")
@RequiredArgsConstructor
public class SubTaskController {
    private final SubTaskService subTaskService;

    /**
     * Создает новую подзадачу для заданной задачи.
     *
     * @param taskId Идентификатор основной задачи.
     * @param newSubTaskDto DTO с данными для новой подзадачи.
     * @return ResponseEntity с DTO созданной подзадачи.
     */
    @PostMapping(path = "/task/{taskId}")
    public ResponseEntity<SubTaskDto> addSubTaskByTaskId(@PathVariable(name = "taskId")
                                                         Long taskId,
                                                         @RequestBody
                                                         NewSubTaskDto newSubTaskDto) {
        return ResponseEntity.ok()
                .body(subTaskService.addSubTaskByTaskId(taskId, newSubTaskDto));
    }

    /**
     * Возвращает подзадачу по ее уникальному идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи.
     * @return ResponseEntity с DTO запрашиваемой подзадачи.
     */
    @GetMapping(path = "/{subTaskId}")
    public ResponseEntity<SubTaskDto> getSubTaskById(@PathVariable(name = "subTaskId") Long subTaskId) {

        return ResponseEntity.ok().body(subTaskService.getSubTaskById(subTaskId));

    }

    /**
     * Возвращает список подзадач для заданной задачи, отфильтрованных по заданным критериям.
     *
     * @param taskId Идентификатор задачи, для которой запрашиваются подзадачи.
     * @param filter Фильтры для выборки подзадач.
     * @return ResponseEntity с списком DTO подзадач.
     */
    @GetMapping(path = "/task/{taskId}")
    public ResponseEntity<List<SubTaskDto>> getSubTasksByFilterAndTaskId(@PathVariable(name = "taskId")
                                                                         Long taskId,
                                                                         @RequestBody
                                                                         QueryDslFilterDto filter) {
        return ResponseEntity.ok().body(subTaskService.getSubTasksByFilterAndTaskId(taskId, filter));
    }

    /**
     * Обновляет информацию о подзадаче по ее идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи для обновления.
     * @param subTaskDto DTO с обновленными данными подзадачи.
     * @return ResponseEntity с обновленным DTO подзадачи.
     */
    @PatchMapping(path = "/{subTaskId}")
    public ResponseEntity<SubTaskDto> updateSubTaskById(@PathVariable(name = "subTaskId")
                                                        Long subTaskId,
                                                        @RequestBody
                                                        SubTaskDto subTaskDto) {
        return ResponseEntity.ok()
                .body(subTaskService.updateSubTaskById(subTaskId, subTaskDto));
    }

    /**
     * Удаляет подзадачу по ее идентификатору.
     *
     * @param subTaskId Идентификатор подзадачи для удаления.
     * @return ResponseEntity с статусом No Content, указывающим на успешное удаление.
     */
    @DeleteMapping(path = "/{subTaskId}")
    public ResponseEntity<Void> deleteSubTaskById(@PathVariable(name = "subTaskId")
                                                  Long subTaskId) {
        subTaskService.deleteSubTaskById(subTaskId);
        return ResponseEntity.noContent().build();
    }

}
