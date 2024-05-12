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
import antonfeklichev.tasktrackerapp.repository.SubTaskRepository;
import antonfeklichev.tasktrackerapp.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class TaskServiceImplIntegrationTest {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    SubTaskRepository subTaskRepository;

    @Test
    public void addTask_ShouldSaveTaskAndReturnTaskDto() {
        // Given
        NewTaskDto newTaskDto = new NewTaskDto("Complete Integration Test",
                "Description",
                TaskStatus.NEW);

        // When
        TaskDto result = taskService.addTask(newTaskDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(newTaskDto.name());
        assertThat(result.description()).isEqualTo(newTaskDto.description());
        assertThat(result.status()).isEqualTo(newTaskDto.status());

        // Verify in database
        var savedTask = taskRepository.findById(result.id()).orElse(null);
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getName()).isEqualTo(newTaskDto.name());
        assertThat(savedTask.getDescription()).isEqualTo(newTaskDto.description());
        assertThat(savedTask.getStatus()).isEqualTo(newTaskDto.status());
    }

    @Test
    public void getTaskById_ShouldReturnTaskDto_WhenTaskExists() {
        // Given
        Task newTask = new Task();
        newTask.setName("Sample Task");
        newTask.setDescription("Sample Task Description");
        newTask.setStatus(TaskStatus.NEW);
        Task savedTask = taskRepository.save(newTask);

        // When
        TaskDto result = taskService.getTaskById(savedTask.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedTask.getId());
        assertThat(result.name()).isEqualTo(savedTask.getName());
        assertThat(result.description()).isEqualTo(savedTask.getDescription());
        assertThat(result.status()).isEqualTo(savedTask.getStatus());
    }

    @Test
    public void getTaskById_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        // Given
        Long nonExistentTaskId = -1L;

        // When
        Throwable thrown = catchThrowable(() -> {
            taskService.getTaskById(nonExistentTaskId);
        });

        // Then
        assertThat(thrown).isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    public void getTasksByFilter_ShouldReturnFilteredTasks() {
        // Given
        Task task1 = new Task(null, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(null, "Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        Task task3 = new Task(null, "Complete the task", "Description 3", TaskStatus.NEW);
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        QueryDslFilterDto filter = new QueryDslFilterDto(TaskStatus.NEW, "Complete");

        // When
        List<TaskDto> filteredTasks = taskService.getTasksByFilter(filter);

        // Then
        assertThat(filteredTasks).isNotEmpty();
        assertThat(filteredTasks.size()).isEqualTo(1);
        assertThat(filteredTasks.get(0).name()).contains("Complete");
        assertThat(filteredTasks.get(0).status()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    public void updateTaskById_ShouldUpdateTaskDetails_WhenNoSubTasksInProgress() {
        // Given
        Task task = new Task(null, "Initial Task", "Initial Description", TaskStatus.IN_PROGRESS);
        Task savedTask = taskRepository.save(task);
        TaskDto taskDto = new TaskDto(savedTask.getId(), "Updated Task", "Updated Description", TaskStatus.DONE);

        // When
        TaskDto updatedTask = taskService.updateTaskById(savedTask.getId(), taskDto);

        // Then
        assertThat(updatedTask.name()).isEqualTo("Updated Task");
        assertThat(updatedTask.description()).isEqualTo("Updated Description");
        assertThat(updatedTask.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void updateTaskById_ShouldThrowUpdateTaskException_WhenSubTasksAreInProgress() {
        // Given
        Task task = new Task(1l, "Task with SubTask", "Description", TaskStatus.IN_PROGRESS);
        task = taskRepository.save(task);
        SubTask subTask = new SubTask(null, "Subtask", "Description", TaskStatus.IN_PROGRESS, task);
        subTaskRepository.save(subTask);
        TaskDto taskDto = new TaskDto(task.getId(), "Task with SubTask", "Description", TaskStatus.DONE);

        // When & Then
        assertThrows(UpdateTaskException.class, () -> taskService.updateTaskById(1l, taskDto));
    }

    @Test
    public void updateTaskById_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        // When & Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskById(-1L, new TaskDto(-1L, "Nonexistent Task", "No Desc", TaskStatus.NEW)));
    }

    @Test
    public void deleteTaskById_WhenAllSubTasksAreDone_ShouldDeleteTask() {
        // Given
        Task task = new Task(null, "Task to delete", "Description", TaskStatus.NEW);
        Task savedTask = taskRepository.save(task);
        assertThat(taskRepository.existsById(savedTask.getId())).isTrue();

        // When
        taskService.deleteTaskById(savedTask.getId());

        // Then
        assertThat(taskRepository.existsById(savedTask.getId())).isFalse();
    }

    @Test
    public void deleteTaskById_WhenSubTasksAreNotDone_ShouldThrowException() {
        // Given
        Task task = new Task(1L, "Task with active subtasks", "Description", TaskStatus.NEW);
        task = taskRepository.save(task);
        SubTask subTask = new SubTask(2L,
                "Active subtask",
                "Description",
                TaskStatus.IN_PROGRESS, task);

        subTaskRepository.save(subTask);


        // When & Then
        Throwable thrown = catchThrowable(() -> {
            taskService.deleteTaskById(1L);
        });

        assertThat(thrown).isInstanceOf(DeleteTaskException.class)
                .hasMessageContaining("Delete active SubTasks of this Task first");
        assertThat(taskRepository.existsById(task.getId())).isTrue();

    }

}
