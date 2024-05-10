package antonfeklichev.tasktrackerapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UpdateTaskException extends RuntimeException{
    public UpdateTaskException(String msg) {
        super(msg);
    }
}
