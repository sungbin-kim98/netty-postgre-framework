package errors;

import java.io.IOException;

public class InvalidInputException extends IOException {
    public InvalidInputException(String message) {
        super(message);
    }
}