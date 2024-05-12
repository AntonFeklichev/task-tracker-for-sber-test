package antonfeklichev.tasktrackerapp.repository;

import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностями подзадач в базе данных.
 * <p>
 * Наследует функциональность от {@link JpaRepository} и {@link QuerydslPredicateExecutor} для
 * предоставления стандартных методов управления сущностями и выполнения запросов с использованием Querydsl.
 * </p>
 * <p>
 * Определяет дополнительный запрос для получения списка подзадач, не соответствующих определенному статусу,
 * связанных с конкретной задачей.
 * </p>
 */
@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long>, QuerydslPredicateExecutor<SubTask> {

    /**
     * Возвращает список подзадач для заданной задачи, исключая подзадачи с указанным статусом.
     * Этот метод полезен для проверки состояний связанных подзадач, например, перед обновлением статуса основной задачи.
     *
     * @param taskId Идентификатор основной задачи, для которой нужно найти подзадачи.
     * @param status Статус, который не должны иметь подзадачи.
     * @return Список подзадач, удовлетворяющих критериям запроса.
     */
    @Query("SELECT s " +
           "FROM SubTask s " +
           "WHERE s.task.id = :taskId AND s.status <> :status")
    List<SubTask> getSubTaskByTaskIdNotEqualStatus(Long taskId, TaskStatus status);

}
