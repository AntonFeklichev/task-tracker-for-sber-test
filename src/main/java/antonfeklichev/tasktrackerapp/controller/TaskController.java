package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> addTask(@RequestBody NewTaskDto newTaskDto) {
        log.info("Получен запрос на создание задачи {}",newTaskDto);
        return ResponseEntity.ok()
                .body(taskService.addTask(newTaskDto));
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "taskId") Long taskId) {
        return ResponseEntity.ok().body(taskService.getTaskById(taskId));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByFilter(@RequestBody QueryDslFilterDto filter) {
        return ResponseEntity.ok()
                .body(taskService.getTasksByFilter(filter));
    }

    @PatchMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> updateTaskById(@PathVariable(name = "taskId")
                                                  Long taskId,
                                                  @RequestBody
                                                  TaskDto taskDto) {
        return ResponseEntity.ok()
                .body(taskService.updateTaskById(taskId, taskDto));

    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable(name = "taskId") Long taskId) {

        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }


}
