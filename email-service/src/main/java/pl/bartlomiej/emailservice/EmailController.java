package pl.bartlomiej.emailservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.emailservice.domain.StandardEmail;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    @PostMapping("/standard")
    public ResponseEntity<ResponseModel<StandardEmail>> sendStandardEmail(@RequestBody @Valid final StandardEmail standardEmail) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<StandardEmail>(HttpStatus.OK)
                        .message("A successful email was sent.")
                        .body()
                        .build()
        );
    }
}