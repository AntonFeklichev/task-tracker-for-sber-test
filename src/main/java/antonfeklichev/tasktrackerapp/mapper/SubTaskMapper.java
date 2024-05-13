package antonfeklichev.tasktrackerapp.mapper;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;
import antonfeklichev.tasktrackerapp.entity.SubTask;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью {@link SubTask} и DTO {@link SubTaskDto}, {@link NewSubTaskDto}.
 * <p>
 * Аннотация {@link Mapper} указывает MapStruct, что этот интерфейс является маппером и должен быть реализован с поддержкой Spring.
 * </p>
 *
 * @see SubTask
 * @see SubTaskDto
 * @see NewSubTaskDto
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubTaskMapper {

    /**
     * Преобразует {@link NewSubTaskDto} в {@link SubTask}.
     *
     * @param dto объект {@link NewSubTaskDto}, содержащий данные для создания новой подзадачи.
     * @return экземпляр {@link SubTask}, сформированный на основе данных из {@link NewSubTaskDto}.
     */
    SubTask toSubTask(NewSubTaskDto dto);

    /**
     * Преобразует {@link SubTask} в {@link SubTaskDto}, включая маппинг идентификатора задачи.
     *
     * @param subTask объект {@link SubTask}, данные которого необходимо преобразовать в DTO.
     * @return экземпляр {@link SubTaskDto}, содержащий данные из {@link SubTask}, включая идентификатор родительской задачи.
     */
    @Mapping(source = "task.id", target = "taskId")
    SubTaskDto toSubTaskDto(SubTask subTask);

    /**
     * Обновляет существующий экземпляр {@link SubTask} на основе данных из {@link SubTaskDto}.
     * <p>
     * Метод аннотирован {@link BeanMapping} с стратегией {@link NullValuePropertyMappingStrategy IGNORE}, что означает,
     * что нулевые значения в {@link SubTaskDto} не будут переноситься в {@link SubTask}, сохраняя текущие значения свойств {@link SubTask}.
     * </p>
     *
     * @param subTask    объект {@link SubTask}, который требуется обновить.
     * @param subTaskDto DTO, содержащее данные для обновления подзадачи.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchSubTask(@MappingTarget SubTask subTask, SubTaskDto subTaskDto);

}
