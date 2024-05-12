package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.QueryDslFilterDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import antonfeklichev.tasktrackerapp.exception.SubTaskNotFoundException;
import antonfeklichev.tasktrackerapp.exception.TaskNotFoundException;
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@Testcontainers
public class SubTaskServiceImplIntegrationTest {

    @Autowired
    SubTaskService subTaskService;

    @Autowired
    SubTaskRepository subTaskRepository;

    @Autowired
    TaskRepository taskRepository;


    @Test
    public void addSubTaskByTaskId_ShouldSaveSubTaskAndReturnSubTaskDto() {
        // Given

        Task task = new Task(null,
                "Main Task",
                "Main task description",
                TaskStatus.NEW);
        Task savedTask = taskRepository.save(task);

        NewSubTaskDto newSubTaskDto = new NewSubTaskDto("SubTask Name",
                "SubTask Description",
                TaskStatus.NEW);

        // When
        SubTaskDto result = subTaskService.addSubTaskByTaskId(savedTask.getId(), newSubTaskDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(newSubTaskDto.name());
        assertThat(result.description()).isEqualTo(newSubTaskDto.description());
        assertThat(result.status()).isEqualTo(newSubTaskDto.status());
        assertThat(result.taskId()).isEqualTo(savedTask.getId());

        // Verify in database
        var savedSubTask = subTaskRepository.findById(result.id()).orElse(null);
        assertThat(savedSubTask).isNotNull();
        assertThat(savedSubTask.getName()).isEqualTo(newSubTaskDto.name());
        assertThat(savedSubTask.getDescription()).isEqualTo(newSubTaskDto.description());
        assertThat(savedSubTask.getStatus()).isEqualTo(newSubTaskDto.status());
        assertThat(savedSubTask.getTask().getId()).isEqualTo(savedTask.getId());
    }

    @Test
    public void getSubTaskById_ShouldReturnSubTaskDto_WhenSubTaskExists() {
        // Given
        Task task = new Task(null, "Main Task", "Description", TaskStatus.NEW);
        task = taskRepository.save(task);
        SubTask newSubTask = new SubTask(null, "SubTask", "Description of SubTask", TaskStatus.NEW, task);
        SubTask savedSubTask = subTaskRepository.save(newSubTask);

        // When
        SubTaskDto result = subTaskService.getSubTaskById(savedSubTask.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedSubTask.getId());
        assertThat(result.name()).isEqualTo(savedSubTask.getName());
        assertThat(result.description()).isEqualTo(savedSubTask.getDescription());
        assertThat(result.status()).isEqualTo(savedSubTask.getStatus());
    }

    @Test
    public void getSubTaskById_ShouldThrowTaskNotFoundException_WhenSubTaskDoesNotExist() {
        // Given
        Long nonExistentSubTaskId = -1L;

        // When
        Throwable thrown = catchThrowable(() -> subTaskService.getSubTaskById(nonExistentSubTaskId));

        // Then
        assertThat(thrown).isInstanceOf(SubTaskNotFoundException.class)
                .hasMessageContaining("SubTask not found");
    }

    @Test
    public void getSubTasksByFilterAndTaskId_ShouldReturnFilteredSubTasks() {
        // Given
        Task task = new Task(null, "Main Task", "Main description", TaskStatus.NEW);
        task = taskRepository.save(task);
        subTaskRepository.save(new SubTask(null, "SubTask One", "First Description", TaskStatus.NEW, task));
        subTaskRepository.save(new SubTask(null, "SubTask Two", "Second Description", TaskStatus.DONE, task));
        subTaskRepository.save(new SubTask(null, "SubTask Three", "Third Description", TaskStatus.IN_PROGRESS, task));

        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.DONE, "Two");

        // When
        List<SubTaskDto> result = subTaskService.getSubTasksByFilterAndTaskId(task.getId(), filter);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("SubTask Two");
        assertThat(result.get(0).description()).isEqualTo("Second Description");
        assertThat(result.get(0).status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void updateSubTaskById_ShouldUpdateAndReturnSubTaskDto() {
        // Given
        Task task = taskRepository.save(new Task(null, "Parent Task", "Description", TaskStatus.NEW));
        SubTask subTask = subTaskRepository.save(new SubTask(null, "Initial Name", "Initial Description", TaskStatus.NEW, task));
        SubTaskDto updatedSubTaskDto = new SubTaskDto(subTask.getId(), "Updated Name", "Updated Description", TaskStatus.DONE, task.getId());

        // When
        SubTaskDto result = subTaskService.updateSubTaskById(subTask.getId(), updatedSubTaskDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(subTask.getId());
        assertThat(result.name()).isEqualTo("Updated Name");
        assertThat(result.description()).isEqualTo("Updated Description");
        assertThat(result.status()).isEqualTo(TaskStatus.DONE);

        // Verify update in the database
        SubTask updatedSubTask = subTaskRepository.findById(subTask.getId()).orElseThrow();
        assertThat(updatedSubTask.getName()).isEqualTo("Updated Name");
        assertThat(updatedSubTask.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedSubTask.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void updateSubTaskById_ShouldThrowTaskNotFoundException_WhenSubTaskDoesNotExist() {
        // Given
        Long nonExistentSubTaskId = -1L;
        SubTaskDto subTaskDto = new SubTaskDto(nonExistentSubTaskId, "Non-existent", "Does not exist", TaskStatus.NEW, -1L);

        // When & Then
        Throwable thrown = catchThrowable(() -> subTaskService.updateSubTaskById(nonExistentSubTaskId, subTaskDto));

        assertThat(thrown).isInstanceOf(SubTaskNotFoundException.class)
                .hasMessageContaining("SubTask not found");

    }


    @Test
    public void deleteSubTaskById_ShouldDeleteSubTask() {
        // Given
        Task task = new Task(null, "Parent Task", "Parent Description", TaskStatus.NEW);
        task = taskRepository.save(task);
        SubTask subTask = new SubTask(null, "SubTask Name", "SubTask Description", TaskStatus.NEW, task);
        subTask = subTaskRepository.save(subTask);

        // When
        subTaskService.deleteSubTaskById(subTask.getId());

        // Then
        assertThat(subTaskRepository.existsById(subTask.getId())).isFalse();
    }


}
