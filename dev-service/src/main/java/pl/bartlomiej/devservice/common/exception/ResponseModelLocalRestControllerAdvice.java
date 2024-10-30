package pl.bartlomiej.devservice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiej.devservice.common.exception.apiexception.InvalidApplicationRequestStatusException;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestControllerAdvice
public class ResponseModelLocalRestControllerAdvice {

    @ExceptionHandler(InvalidApplicationRequestStatusException.class)
    public ResponseEntity<ResponseModel<Void>> handleInvalidApplicationRequestStatusException(InvalidApplicationRequestStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel.buildBasicErrorResponseModel(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
