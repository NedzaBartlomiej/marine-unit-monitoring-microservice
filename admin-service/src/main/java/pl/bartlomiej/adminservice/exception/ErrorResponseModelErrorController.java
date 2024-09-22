package pl.bartlomiej.adminservice.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorResponseModelErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponseModel> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponseModel(
                        httpStatus,
                        statusCode,
                        httpStatus.getReasonPhrase()
                ));
    }
}
