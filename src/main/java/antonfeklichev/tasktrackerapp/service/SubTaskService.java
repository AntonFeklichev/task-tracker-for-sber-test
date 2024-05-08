package antonfeklichev.tasktrackerapp.service;

import antonfeklichev.tasktrackerapp.dto.NewSubTaskDto;
import antonfeklichev.tasktrackerapp.dto.SubTaskDto;

import java.util.List;

public interface SubTaskService {
    SubTaskDto addSubTaskByTaskId(Long taskId, NewSubTaskDto newSubTaskDto);

    SubTaskDto getSubTaskById(Long subTaskId);

    List<SubTaskDto> getAllSubTaskByTaskId(Long taskId);

    SubTaskDto updateSubTaskById(Long subTaskId, SubTaskDto subTaskDto);

    void deleteSubTaskById(Long subTaskId);
}
