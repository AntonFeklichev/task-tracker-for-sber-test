package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.QSubTask;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.mapper.SubTaskMapper;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubTaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubTaskRepository subTaskRepository;

    @Mock
    private SubTaskMapper subTaskMapper;

    @InjectMocks
    private SubTaskServiceImpl subTaskService;


    @Test
    void addSubTaskByTaskIdSuccessful() {
        // Given
        Long taskId = 1L;
        NewSubTaskDto newSubTaskDto = new NewSubTaskDto("SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS);
        Task task = new Task(taskId, "Task Name", "Task Description", TaskStatus.NEW);
        SubTask subTask = new SubTask(null, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, task);
        SubTask savedSubTask = new SubTask(2L, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, task);
        SubTaskDto expectedDto = new SubTaskDto(2L, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subTaskMapper.toSubTask(newSubTaskDto)).thenReturn(subTask);
        when(subTaskRepository.save(subTask)).thenReturn(savedSubTask);
        when(subTaskMapper.toSubTaskDto(savedSubTask)).thenReturn(expectedDto);

        // When
        SubTaskDto result = subTaskService.addSubTaskByTaskId(taskId, newSubTaskDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(taskRepository).findById(taskId);
        verify(subTaskRepository).save(subTask);
        verify(subTaskMapper).toSubTaskDto(savedSubTask);
    }

    @Test
    void addSubTaskByTaskIdTaskNotFound() {
        // Given
        Long taskId = 1L;
        NewSubTaskDto newSubTaskDto = new NewSubTaskDto("SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> subTaskService.addSubTaskByTaskId(taskId, newSubTaskDto));
    }

    @Test
    void getSubTaskByIdFound() {
        // Given
        Long subTaskId = 1L;
        SubTask subTask = new SubTask(subTaskId, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, null);
        SubTaskDto expectedDto = new SubTaskDto(subTaskId, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, null);

        when(subTaskRepository.findById(subTaskId)).thenReturn(Optional.of(subTask));
        when(subTaskMapper.toSubTaskDto(subTask)).thenReturn(expectedDto);

        // When
        SubTaskDto result = subTaskService.getSubTaskById(subTaskId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void getSubTaskByIdNotFound() {
        // Given
        Long subTaskId = 1L;
        when(subTaskRepository.findById(subTaskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> subTaskService.getSubTaskById(subTaskId));
    }


    @Test
    void getSubTasksByFilterAndTaskId() {
        // Given
        Long taskId = 1L;
        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.DONE, "Important");
        List<SubTask> subTasks = List.of(
                new SubTask(1L, "Important SubTask", "Description", TaskStatus.DONE, null),
                new SubTask(2L, "Another SubTask", "Description", TaskStatus.DONE, null)
        );
        List<SubTaskDto> expectedDto = List.of(
                new SubTaskDto(1L, "Important SubTask", "Description", TaskStatus.DONE, taskId),
                new SubTaskDto(2L, "Another SubTask", "Description", TaskStatus.DONE, taskId)
        );

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QSubTask.subTask.task.id.eq(taskId));
        predicate.and(QSubTask.subTask.status.eq(filter.status()));
        predicate.and(QSubTask.subTask.name.containsIgnoreCase(filter.name()));

        when(subTaskRepository.findAll(Mockito.any(BooleanBuilder.class))).thenReturn(subTasks);
        when(subTaskMapper.toSubTaskDto(Mockito.any(SubTask.class))).thenAnswer(invocation -> {
            SubTask st = invocation.getArgument(0);
            return new SubTaskDto(st.getId(), st.getName(), st.getDescription(), st.getStatus(), taskId);
        });

        // When
        List<SubTaskDto> result = subTaskService.getSubTasksByFilterAndTaskId(taskId, filter);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDto, result);
        verify(subTaskRepository).findAll(Mockito.any(BooleanBuilder.class));
    }

    @Test
    void updateSubTaskByIdFound() {
        // Given
        Long subTaskId = 1L;
        SubTask subTask = new SubTask(subTaskId, "SubTask Name", "SubTask Description", TaskStatus.IN_PROGRESS, null);
        SubTask updatedSubTask = new SubTask(subTaskId, "Updated Name", "Updated Description", TaskStatus.DONE, null);
        SubTaskDto subTaskDto = new SubTaskDto(subTaskId, "Updated Name", "Updated Description", TaskStatus.DONE, null);

        when(subTaskRepository.findById(subTaskId)).thenReturn(Optional.of(subTask));
        when(subTaskRepository.save(subTask)).thenReturn(updatedSubTask);
        when(subTaskMapper.toSubTaskDto(updatedSubTask)).thenReturn(subTaskDto);

        // When
        SubTaskDto result = subTaskService.updateSubTaskById(subTaskId, subTaskDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals(TaskStatus.DONE, result.status());
        verify(subTaskMapper).patchSubTask(subTask, subTaskDto);
        verify(subTaskRepository).save(subTask);
    }

    @Test
    void updateSubTaskByIdNotFound() {
        // Given
        Long subTaskId = 1L;
        SubTaskDto subTaskDto = new SubTaskDto(subTaskId, "Name", "Description", TaskStatus.IN_PROGRESS, null);

        when(subTaskRepository.findById(subTaskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TaskNotFoundException.class, () -> subTaskService.updateSubTaskById(subTaskId, subTaskDto));
    }

    @Test
    void deleteSubTaskById() {
        // Given
        Long subTaskId = 1L;

        // When
        subTaskService.deleteSubTaskById(subTaskId);

        // Then
        verify(subTaskRepository).deleteById(subTaskId);
    }


}
