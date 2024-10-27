package pl.bartlomiej.devservice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiej.devservice.common.exception.apiexception.InvalidApplicationRequestStatusException;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.ErrorResponseModel;

@RestControllerAdvice
public class ErrorResponseModelLocalRestControllerAdvice {

    @ExceptionHandler(InvalidApplicationRequestStatusException.class)
    public ResponseEntity<ErrorResponseModel> handleInvalidApplicationRequestStatusException(InvalidApplicationRequestStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseModel(
                        HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                ));
    }
}
