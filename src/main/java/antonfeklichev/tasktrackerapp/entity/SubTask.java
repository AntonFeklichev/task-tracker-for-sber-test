package antonfeklichev.tasktrackerapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SubTask extends Task {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    public SubTask(Long id, String name, String description, TaskStatus status, Task task) {
        super(id, name, description, status);
        this.task = task;
    }

    public SubTask(Task task) {
        this.task = task;
    }
}
