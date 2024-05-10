package antonfeklichev.tasktrackerapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeleteTaskException extends RuntimeException {
    public DeleteTaskException(String msg) {
        super(msg);
    }
}
