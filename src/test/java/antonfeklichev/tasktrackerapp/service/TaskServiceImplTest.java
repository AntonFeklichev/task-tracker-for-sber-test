package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.DeleteTaskException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.UpdateTaskException;
import antonfeklichev.tasktrackerapp.mapper.TaskMapper;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    SubTaskRepository subTaskRepository;
    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    TaskServiceImpl taskServiceImpl;

    @Test
    void addTaskTest() {
        // Given
        NewTaskDto newTaskDto = new NewTaskDto("Task Name", "Task Description", TaskStatus.NEW);
        Task task = new Task(1L, "Task Name", "Task Description", TaskStatus.NEW);
        TaskDto expectedDto = new TaskDto(1L, "Task Name", "Task Description", TaskStatus.NEW);

        when(taskMapper.toTask(newTaskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskDto(task)).thenReturn(expectedDto);

        // When
        TaskDto result = taskServiceImpl.addTask(newTaskDto);

        // Then
        assertEquals(expectedDto, result);

        verify(taskMapper).toTask(newTaskDto);
        verify(taskRepository).save(task);
        verify(taskMapper).toTaskDto(task);
    }


    @Test
    void getTaskByIdFoundTest() {
        // Given
        Long taskId = 1L;
        Task task = new Task(1L, "Test Task", "Description", TaskStatus.NEW);
        TaskDto taskDto = new TaskDto(1L, "Test Task", "Description", TaskStatus.NEW);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        // When
        TaskDto result = taskServiceImpl.getTaskById(taskId);

        // Then
        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void getTaskByIdNotFoundTest() {
        // Given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(TaskNotFoundException.class, () -> {
            taskServiceImpl.getTaskById(taskId);
        });
    }


    @Test
    void getTasksByFilterWithStatus() {
        // Given
        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.DONE, "Important Task");
        Task task = new Task(1L, "Important Task", "Description", TaskStatus.DONE);
        List<Task> tasks = List.of(task);
        TaskDto taskDto = new TaskDto(1L, "Important Task", "Description", TaskStatus.DONE);
        List<TaskDto> expectedDto = List.of(taskDto);

        when(taskRepository.findAll(any(BooleanBuilder.class))).thenReturn(tasks);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        // When
        List<TaskDto> result = taskServiceImpl.getTasksByFilter(filter);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedDto, result);


        verify(taskRepository).findAll(any(BooleanBuilder.class));
        verify(taskMapper, times(tasks.size())).toTaskDto(any(Task.class));
    }


    @Test
    void updateTaskById_ShouldUpdateTask_WhenNoActiveSubTasks() {
        // Given
        Long taskId = 1L;
        Task task = new Task(taskId, "Old Name", "Old Description", TaskStatus.NEW);
        TaskDto taskDto = new TaskDto(taskId, "Updated Name", "Updated Description", TaskStatus.DONE);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE)).thenReturn(Collections.emptyList());
        when(taskMapper.toTaskDto(any())).thenReturn(taskDto);

        // When
        TaskDto updatedTask = taskServiceImpl.updateTaskById(taskId, taskDto);

        // Then
        assertThat(updatedTask.name()).isEqualTo(taskDto.name());
        assertThat(updatedTask.status()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskById_ShouldThrowException_WhenActiveSubTasksExist() {
        // Given
        Long taskId = 1L;
        Task task = new Task(taskId, "Old Name", "Old Description", TaskStatus.NEW);
        TaskDto taskDto = new TaskDto(taskId, "Updated Name", "Updated Description", TaskStatus.DONE);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE)).thenReturn(Collections.singletonList(new SubTask()));

        // When & Then
        assertThrows(UpdateTaskException.class, () -> taskServiceImpl.updateTaskById(taskId, taskDto));
    }

    @Test
    void deleteTaskById_ShouldDeleteTask_WhenNoActiveSubTasks() {
        // Given
        Long taskId = 1L;
        when(subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE)).thenReturn(Collections.emptyList());

        // When
        taskServiceImpl.deleteTaskById(taskId);

        // Then
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTaskById_ShouldThrowException_WhenActiveSubTasksExist() {
        // Given
        Long taskId = 1L;
        when(subTaskRepository.getSubTaskByTaskIdNotEqualStatus(taskId, TaskStatus.DONE)).thenReturn(List.of(new SubTask()));

        // When & Then
        assertThatThrownBy(() -> taskServiceImpl.deleteTaskById(taskId))
                .isInstanceOf(DeleteTaskException.class)
                .hasMessageContaining("Delete active SubTasks of this Task first");
    }

}
