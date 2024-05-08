package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping(path = "/add")
    public ResponseEntity<TaskDto> addTask(@RequestBody NewTaskDto newTaskDto) {
        return ResponseEntity.ok()
                .body(taskService.addTask(newTaskDto));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok()
                .body(taskService.getAllTasks());
    }

    @PatchMapping(path = "/{taskId}")
    public ResponseEntity<TaskDto> updateTaskStatusByTaskId(@PathVariable(name = "taskId")
                                                            Long taskId,
                                                            @RequestBody
                                                            TaskDto taskDto) {
        return ResponseEntity.ok()
                .body(taskService.updateTaskStatusByTaskId(taskId, taskDto));

    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity deleteTaskById(@PathVariable(name = "taskId") Long taskId) {

        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }


}
