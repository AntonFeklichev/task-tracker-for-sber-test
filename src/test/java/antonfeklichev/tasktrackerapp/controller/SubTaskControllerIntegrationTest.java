package antonfeklichev.tasktrackerapp.controller;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.service.SubTaskService;
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
public class SubTaskControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    SubTaskService subTaskService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void addSubTaskByTaskId() throws Exception {

        //Given
        Long taskId = 1L;
        NewSubTaskDto newSubTaskDto = new NewSubTaskDto("Subtask Name", "Description", TaskStatus.NEW);
        SubTaskDto returnedSubTaskDto = new SubTaskDto(1L,
                "Subtask Name",
                "Description",
                TaskStatus.NEW,
                taskId);

        given(subTaskService.addSubTaskByTaskId(taskId, newSubTaskDto)).willReturn(returnedSubTaskDto);

        //When & Then
        mockMvc.perform(post("/api/v1/subtasks/task/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSubTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Subtask Name"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    public void getSubTaskById() throws Exception {

        //Given
        Long subTaskId = 1L;
        SubTaskDto subTaskDto = new SubTaskDto(subTaskId,
                "Subtask Name",
                "Description",
                TaskStatus.NEW,
                1L);

        given(subTaskService.getSubTaskById(subTaskId)).willReturn(subTaskDto);

        //When & Then
        mockMvc.perform(get("/api/v1/subtasks/{subTaskId}", subTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subTaskId))
                .andExpect(jsonPath("$.name").value("Subtask Name"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    public void getSubTasksByFilterAndTaskId() throws Exception {

        //Given
        Long taskId = 1L;
        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.IN_PROGRESS, "Update");
        List<SubTaskDto> expectedSubTasks = List.of(
                new SubTaskDto(1L, "Update SubTask 1", "description 1", TaskStatus.IN_PROGRESS, taskId),
                new SubTaskDto(2L, "Update SubTask 2", "description 2", TaskStatus.IN_PROGRESS, taskId)
        );

        given(subTaskService.getSubTasksByFilterAndTaskId(taskId, filter)).willReturn(expectedSubTasks);

        //When & Then
        mockMvc.perform(get("/api/v1/subtasks/task/{taskId}", taskId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(expectedSubTasks.get(0).id()))
                .andExpect(jsonPath("$[0].name").value("Update SubTask 1"))
                .andExpect(jsonPath("$[1].id").value(expectedSubTasks.get(1).id()))
                .andExpect(jsonPath("$[1].name").value("Update SubTask 2"));
    }

    @Test
    public void updateSubTaskById() throws Exception {

        //Given
        Long subTaskId = 1L;
        SubTaskDto subTaskDto = new SubTaskDto(subTaskId,
                "Updated Subtask Name",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                1L);

        given(subTaskService.updateSubTaskById(subTaskId, subTaskDto)).willReturn(subTaskDto);

        //When & Then
        mockMvc.perform(patch("/api/v1/subtasks/{subTaskId}", subTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Subtask Name"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void deleteSubTaskById() throws Exception {

        //Given
        Long subTaskId = 1L;

        //When & Then
        mockMvc.perform(delete("/api/v1/subtasks/{subTaskId}", subTaskId))
                .andExpect(status().isNoContent());
    }

}
