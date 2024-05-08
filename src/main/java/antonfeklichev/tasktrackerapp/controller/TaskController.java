package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> addTask(@RequestBody NewTaskDto newTaskDto) {
        return ResponseEntity.ok()
                .body(taskService.addTask(newTaskDto));
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "taskId") Long taskId) {
        return ResponseEntity.ok().body(taskService.getTaskById(taskId));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok()
                .body(taskService.getAllTasks());
    }

    @PatchMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> updateTaskById(@PathVariable(name = "taskId")
                                                  Long taskId,
                                                  @RequestBody
                                                  TaskDto taskDto) {
        return ResponseEntity.ok()
                .body(taskService.updateTaskById(taskId, taskDto)); //TODO разобрать необходимость taskId

    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable(name = "taskId") Long taskId) {

        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }


}
