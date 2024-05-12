package antonfeklichev.tasktrackerapp.repository;

import antonfeklichev.tasktrackerapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления сущностями задач ({@link Task}) в базе данных.
 * <p>
 * Наследует функциональность от {@link JpaRepository} и {@link QuerydslPredicateExecutor} для
 * предоставления стандартных методов управления сущностями и выполнения запросов с использованием Querydsl.
 * </p>
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
}
