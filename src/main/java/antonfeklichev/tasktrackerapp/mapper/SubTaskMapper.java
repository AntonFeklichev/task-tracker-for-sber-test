package antonfeklichev.tasktrackerapp.mapper;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import antonfeklichev.tasktrackerapp.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubTaskMapper {
    @Mapping(source = "taskId", target = "task.id")//, qualifiedByName = "taskIdToTask")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    SubTask toSubTask(NewSubTaskDto dto);

    @Named("taskIdToTask")
    default Task taskIdToTask(Long taskId) {
        if (taskId == null) {
            return null;
        }
        Task task = new Task();
        task.setId(taskId);
        return task;
    } //TODO Доделать получение Таск

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "task.id", target = "taskId")
    SubTaskDto toSubTaskDto(SubTask subTask);

}
