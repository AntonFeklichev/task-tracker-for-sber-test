package antonfeklichev.tasktrackerapp.mapper;

import antonfeklichev.tasktrackerapp.dto.NewTaskDto;
import antonfeklichev.tasktrackerapp.dto.TaskDto;
import antonfeklichev.tasktrackerapp.entity.Task;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью {@link Task} и DTO {@link TaskDto}, {@link NewTaskDto}.
 * <p>
 * Аннотация {@link Mapper} указывает MapStruct, что этот интерфейс является маппером и должен быть реализован с поддержкой Spring.
 * </p
 *
 * @see Task
 * @see TaskDto
 * @see NewTaskDto
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    /**
     * Преобразует {@link NewTaskDto} в {@link Task}.
     *
     * @param dto объект {@link NewTaskDto}, содержащий данные для создания новой задачи.
     * @return экземпляр {@link Task}, сформированный на основе данных из {@link NewTaskDto}.
     */
    Task toTask(NewTaskDto dto);

    /**
     * Преобразует {@link Task} в {@link TaskDto}.
     *
     * @param task объект {@link Task}, данные которого необходимо преобразовать в DTO.
     * @return экземпляр {@link TaskDto}, содержащий данные из {@link Task}.
     */
    TaskDto toTaskDto(Task task);

    /**
     * Обновляет существующий экземпляр {@link Task} на основе данных из {@link TaskDto}.
     * <p>
     * Метод аннотирован {@link BeanMapping} с стратегией {@link NullValuePropertyMappingStrategy IGNORE}, что означает,
     * что нулевые значения в {@link TaskDto} не будут переноситься в {@link Task}, сохраняя текущие значения свойств {@link Task}.
     * </p>
     *
     * @param task    объект {@link Task}, который требуется обновить.
     * @param taskDto DTO, содержащее данные для обновления задачи.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchTask(@MappingTarget Task task, TaskDto taskDto);
}
