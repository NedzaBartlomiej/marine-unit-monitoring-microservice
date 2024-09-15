package pl.bartlomiej.adminservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponseModel {
    private final HttpStatus httpStatus;
    private final int httpStatusCode;
    private final String errMessage;
    private final LocalDateTime time = LocalDateTime.now();
}
