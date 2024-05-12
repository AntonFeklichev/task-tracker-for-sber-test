package antonfeklichev.tasktrackerapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class TaskTrackerAppExceptionHandler {
    record ErrorResponse(String msg) {
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleDeleteTaskException(DeleteTaskException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleUpdateTaskException(UpdateTaskException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleSubTaskNotFoundException(SubTaskNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

}
