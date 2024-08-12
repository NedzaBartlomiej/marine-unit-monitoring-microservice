package pl.bartlomiej.apiservice.common.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel<T> {

    private final HttpStatus httpStatus;
    private final Integer httpStatusCode;
    private final String message;
    @Builder.Default
    private final LocalDateTime readingTime = now();
    private final Map<String, T> body;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public Map<String, T> getBody() {
        return body;
    }
}