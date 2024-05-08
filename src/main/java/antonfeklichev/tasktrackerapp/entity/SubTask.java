package antonfeklichev.tasktrackerapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubTask extends Task {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id") //TODO Разобрать с Матвеем название
    Task task;

}
