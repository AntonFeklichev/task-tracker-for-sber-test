package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.service.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskServiceImpl taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void addTask() throws Exception {

        //Given
        NewTaskDto newTaskDto = new NewTaskDto("Task Name", "Description", TaskStatus.NEW);
        TaskDto returnedTaskDto = new TaskDto(1L, "Task Name", "Description", TaskStatus.NEW);

        given(taskService.addTask(newTaskDto)).willReturn(returnedTaskDto);

        //When & Then
        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Task Name"));
    }

    @Test
    public void getTaskById() throws Exception {

        //Given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto(taskId, "Task Name", "Description", TaskStatus.NEW);

        given(taskService.getTaskById(taskId)).willReturn(taskDto);

        //When & Then
        mockMvc.perform(get("/api/v1/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value("Task Name"));
    }

    @Test
    public void getTasksByFilter() throws Exception {

        //Given
        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.NEW, "Task 1");
        List<TaskDto> expectedTasks = List.of(
                new TaskDto(1L, "Task 1", "Description 1", TaskStatus.NEW),
                new TaskDto(2L, "Task 2", "Description 2", TaskStatus.NEW)
        );

        given(taskService.getTasksByFilter(filter)).willReturn(expectedTasks);

        //When & Then
        mockMvc.perform(get("/api/v1/task")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(expectedTasks.get(0).id()))
                .andExpect(jsonPath("$[0].name").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(expectedTasks.get(1).id()))
                .andExpect(jsonPath("$[1].name").value("Task 2"));
    }

    @Test
    public void updateTaskById() throws Exception {

        //Given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto(taskId, "Updated Task Name", "Updated Description", TaskStatus.IN_PROGRESS);

        given(taskService.updateTaskById(taskId, taskDto)).willReturn(taskDto);

        //When & Then
        mockMvc.perform(patch("/api/v1/task/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Task Name"));
    }

    @Test
    public void deleteTaskById() throws Exception {
        //Given
        Long taskId = 1L;

        //When & Then
        mockMvc.perform(delete("/api/v1/task/{taskId}", taskId))
                .andExpect(status().isNoContent());
    }


}
