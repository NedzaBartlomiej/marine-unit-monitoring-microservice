package pl.bartlomiej.apiservice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

@RestControllerAdvice
public class RestControllerGlobalErrorHandler {

    @ExceptionHandler(MmsiConflictException.class)
    public ResponseEntity<ResponseModel<Void>> handleMmsiConflictException(MmsiConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseModel.buildBasicErrorResponseModel(HttpStatus.CONFLICT, e.getMessage()));
    }
}