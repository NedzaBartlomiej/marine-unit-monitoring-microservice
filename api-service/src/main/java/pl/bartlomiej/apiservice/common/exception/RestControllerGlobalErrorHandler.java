package pl.bartlomiej.apiservice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.ErrorResponseModel;

@RestControllerAdvice
public class RestControllerGlobalErrorHandler {

    @ExceptionHandler(MmsiConflictException.class)
    public ResponseEntity<ErrorResponseModel> handleMmsiConflictException(MmsiConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseModel(
                        HttpStatus.CONFLICT,
                        HttpStatus.CONFLICT.value(),
                        e.getMessage()
                ));
    }
}