package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.QTask;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.DeleteTaskException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.UpdateTaskException;
import antonfeklichev.tasktrackerapp.mapper.SubTaskMapper;
import antonfeklichev.tasktrackerapp.mapper.TaskMapper;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    @Mock
    SubTaskMapper subTaskMapper;
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

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QTask.task.status.eq(TaskStatus.DONE));
        predicate.and(QTask.task.name.containsIgnoreCase("Important Task"));

        when(taskRepository.findAll(Mockito.any(BooleanBuilder.class))).thenReturn(tasks);
        when(taskMapper.toTaskDto(Mockito.any(Task.class))).thenReturn(taskDto);

        // When
        List<TaskDto> result = taskServiceImpl.getTasksByFilter(filter);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedDto, result);


        verify(taskRepository).findAll(Mockito.any(BooleanBuilder.class));
        verify(taskMapper, times(tasks.size())).toTaskDto(Mockito.any(Task.class));
    }

    @Test
    void updateTaskByIdSuccessfully() {
        // Given
        Long taskId = 1L;
        Task task = new Task(1L, "Task Name", "Description", TaskStatus.IN_PROGRESS);
        TaskDto taskDto = new TaskDto(1L, "Task Name Updated", "Description Updated", TaskStatus.DONE);
        Task savedTask = new Task(1L, "Task Name Updated", "Description Updated", TaskStatus.DONE);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(subTaskRepository.findAll(Mockito.any(BooleanBuilder.class))).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        // When
        TaskDto result = taskServiceImpl.updateTaskById(taskId, taskDto);

        // Then
        assertNotNull(result);
        assertEquals("Task Name Updated", result.name());
        assertEquals(TaskStatus.DONE, result.status());
        verify(taskRepository).save(task);
    }


    @Test
    void updateTaskByIdWithActiveSubTasksShouldThrowException() {
        Long taskId = 1L;
        Task existingTask = new Task(taskId, "Old Name", "Old Description", TaskStatus.IN_PROGRESS);
        TaskDto updateDto = new TaskDto(taskId, "Updated Name", "Updated Description", TaskStatus.DONE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(subTaskRepository.findAll(any(BooleanBuilder.class)))
                .thenReturn(List.of(new SubTask(2L,
                        "Subtask Name",
                        "Subtask Description",
                        TaskStatus.IN_PROGRESS,
                        existingTask)));

        assertThrows(UpdateTaskException.class, () -> taskServiceImpl.updateTaskById(taskId, updateDto));
    }


    @Test
    void deleteTaskByIdWithoutActiveSubTasks() {
        Long taskId = 1L;
        when(subTaskRepository.findAll(any(BooleanBuilder.class))).thenReturn(Collections.emptyList());
        doNothing().when(taskRepository).deleteById(taskId);

        taskServiceImpl.deleteTaskById(taskId);

        verify(taskRepository).deleteById(taskId);
        verify(subTaskRepository).findAll(any(BooleanBuilder.class));
    }

    @Test
    void deleteTaskByIdWithActiveSubTasksShouldThrowException() {
        Long taskId = 1L;
        Task existingTask = new Task(taskId, "Old Name", "Old Description", TaskStatus.IN_PROGRESS);
        List<SubTask> activeSubTasks = List.of(new SubTask(2L,
                "Subtask",
                "Description",
                TaskStatus.IN_PROGRESS,
                existingTask));
        when(subTaskRepository.findAll(any(BooleanBuilder.class))).thenReturn(activeSubTasks);

        assertThrows(DeleteTaskException.class, () -> taskServiceImpl.deleteTaskById(taskId));

        verify(taskRepository, never()).deleteById(any());
        verify(subTaskRepository).findAll(any(BooleanBuilder.class));
    }

}
