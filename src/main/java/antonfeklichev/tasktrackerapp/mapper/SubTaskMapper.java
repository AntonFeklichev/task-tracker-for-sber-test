package antonfeklichev.tasktrackerapp.mapper;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubTaskMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    SubTask toSubTask(NewSubTaskDto dto);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "task.id", target = "taskId")
    SubTaskDto toSubTaskDto(SubTask subTask);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchSubTask(@MappingTarget SubTask subTask, SubTaskDto subTaskDto);

}
