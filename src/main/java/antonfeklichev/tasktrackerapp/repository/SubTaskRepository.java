package antonfeklichev.tasktrackerapp.repository;

import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long>, QuerydslPredicateExecutor<SubTask> {

    @Query("SELECT s " +
           "FROM SubTask s " +
           "WHERE s.task.id = :taskId AND s.status <> :status")
    List<SubTask> getSubTaskByTaskIdNoEqualStatus(Long taskId, TaskStatus status);

}
