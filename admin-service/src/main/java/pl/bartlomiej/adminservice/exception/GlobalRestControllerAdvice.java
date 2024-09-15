package pl.bartlomiej.adminservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponseModel> handleErrorResponseException(ErrorResponseException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponseModel(
                        e.getHttpStatus(),
                        e.getHttpStatus().value(),
                        e.getMessage()
                ));
    }
}
