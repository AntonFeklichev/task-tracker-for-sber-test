package antonfeklichev.tasktrackerapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Сущность представляющая подзадачу.
 * <p>
 * Каждая подзадача является расширением основной задачи и хранится в таблице "subtasks".
 * Эта сущность наследует основные атрибуты задачи, такие как идентификатор, название, описание и статус,
 * и добавляет связь с родительской задачей.
 * </p>
 * <p>
 * Связь с родительской задачей реализована через {@link Task}, при этом подзадача ссылается на задачу
 * с помощью внешнего ключа {@code task_id}. Это позволяет организовать иерархическую структуру задач
 * в приложении.
 * </p>
 *
 */
@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubTask extends Task {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    public SubTask(Long id, String name, String description, TaskStatus status, Task task) {
        super(id, name, description, status);
        this.task = task;
    }

}
