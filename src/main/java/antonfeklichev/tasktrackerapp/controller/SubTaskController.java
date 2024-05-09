package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/subtasks")
@RequiredArgsConstructor
public class SubTaskController {
    private final SubTaskService subTaskService;

    @PostMapping(path = "/task/{taskId}")
    public ResponseEntity<SubTaskDto> addSubTaskByTaskId(@PathVariable(name = "taskId")
                                                         Long taskId,
                                                         @RequestBody
                                                         NewSubTaskDto newSubTaskDto) {
        return ResponseEntity.ok()
                .body(subTaskService.addSubTaskByTaskId(taskId, newSubTaskDto));
    }

    @GetMapping(path = "/{subTaskId}")
    public ResponseEntity<SubTaskDto> getSubTaskById(@PathVariable(name = "subTaskId") Long subTaskId) {

        return ResponseEntity.ok().body(subTaskService.getSubTaskById(subTaskId));
        //TODO разобрать как возвращаются Exception с ResponseEntity
    }

    @GetMapping(path = "/task/{taskId}")
    public ResponseEntity<List<SubTaskDto>> getAllSubTasksByTaskId(@PathVariable(name = "taskId")
                                                                  Long taskId) {
        return ResponseEntity.ok().body(subTaskService.getAllSubTasksByTaskId(taskId));
    }

    @PatchMapping(path = "/{subTaskId}")
    public ResponseEntity<SubTaskDto> updateSubTaskById(@PathVariable(name = "subTaskId")
                                                        Long subTaskId,
                                                        @RequestBody
                                                        SubTaskDto subTaskDto) {
        return ResponseEntity.ok()
                .body(subTaskService.updateSubTaskById(subTaskId, subTaskDto));
    }

    @DeleteMapping(path = "/{subTaskId}")
    public ResponseEntity<Void> deleteSubTaskById(@PathVariable(name = "subTaskId")
                                            Long subTaskId) {
        subTaskService.deleteSubTaskById(subTaskId);
        return ResponseEntity.noContent().build();
    }

}
