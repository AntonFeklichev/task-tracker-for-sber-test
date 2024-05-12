package antonfeklichev.tasktrackerapp.entity;
/**
 * Перечисление статусов задач и подзадач.
 * <p>
 * Каждый статус описывает текущее состояние задачи или подзадачи:
 * </p>
 * <ul>
 *   <li>{@code NEW} - Задача создана и ожидает начала выполнения.</li>
 *   <li>{@code IN_PROGRESS} - Задача в процессе выполнения.</li>
 *   <li>{@code DONE} - Задача выполнена.</li>
 * </ul>
 */
public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE
}
