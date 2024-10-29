package kj.demofunkos.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoBadRequestException  extends FunkoException {
    public FunkoBadRequestException(String message) {
        super(message);
    }
}

