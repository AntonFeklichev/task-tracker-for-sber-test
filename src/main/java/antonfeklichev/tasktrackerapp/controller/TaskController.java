package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


}
