package pl.bartlomiej.emailservice.common.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.emailservice.common.service.EmailService;
import pl.bartlomiej.emailservice.common.service.EmailServiceFactory;
import pl.bartlomiej.mummicroservicecommons.emailintegration.external.model.StandardEmail;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private final EmailServiceFactory emailServiceFactory;

    public EmailController(EmailServiceFactory emailServiceFactory) {
        this.emailServiceFactory = emailServiceFactory;
    }

    @PostMapping("/standard")
    public ResponseEntity<ResponseModel<StandardEmail>> sendStandardEmail(@RequestBody @Valid final StandardEmail standardEmail) {
        EmailService<StandardEmail> standardEmailService = emailServiceFactory.resolveEmailService(StandardEmail.class);
        return ResponseEntity.ok(
                new ResponseModel.Builder<StandardEmail>(HttpStatus.OK, true)
                        .message("A successful email was sent.")
                        .body(standardEmailService.send(standardEmail))
                        .build()
        );
    }
}