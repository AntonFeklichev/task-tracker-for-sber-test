package antonfeklichev.tasktrackerapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * Сущность, представляющая задачу.
 * <p>
 * Эта сущность сохраняется в таблице "tasks" и использует стратегию наследования TABLE_PER_CLASS,
 * что означает, что каждый класс-наследник будет иметь свою собственную таблицу в базе данных.
 * </p>
 * <p>
 * Основные атрибуты задачи включают идентификатор, название, описание и статус.
 * Статус задачи представлен перечислением {@link TaskStatus}, что позволяет управлять
 * жизненным циклом задачи.
 * </p>
 *
 */
@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;


}
