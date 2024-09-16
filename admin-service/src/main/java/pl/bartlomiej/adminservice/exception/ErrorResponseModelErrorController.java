package pl.bartlomiej.adminservice.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorResponseModelErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponseModel> handleError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseModel(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An internal server error occurred, try again and please report it to technical support."
                ));
    }
}
