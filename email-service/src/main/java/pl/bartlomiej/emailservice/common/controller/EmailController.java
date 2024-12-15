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
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;
import pl.bartlomiej.mumcommons.emailintegration.external.model.StandardEmail;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private static final String SUCCESSFUL_EMAIL_WAS_SENT = "A successful email was sent.";
    private final EmailServiceFactory emailServiceFactory;

    public EmailController(EmailServiceFactory emailServiceFactory) {
        this.emailServiceFactory = emailServiceFactory;
    }

    @PostMapping("/standard")
    public ResponseEntity<ResponseModel<StandardEmail>> sendStandardEmail(@RequestBody @Valid final StandardEmail standardEmail) {
        EmailService<StandardEmail> standardEmailService = emailServiceFactory.resolveEmailService(StandardEmail.class);
        return ResponseEntity.ok(
                new ResponseModel.Builder<StandardEmail>(HttpStatus.OK, true)
                        .message(SUCCESSFUL_EMAIL_WAS_SENT)
                        .body(standardEmailService.send(standardEmail))
                        .build()
        );
    }

    @PostMapping("/linked")
    public ResponseEntity<ResponseModel<LinkedEmail>> sendLinkedEmail(@RequestBody @Valid final LinkedEmail linkedEmail) {
        EmailService<LinkedEmail> linkedEmailEmailService = emailServiceFactory.resolveEmailService(LinkedEmail.class);
        return ResponseEntity.ok(
                new ResponseModel.Builder<LinkedEmail>(HttpStatus.OK, true)
                        .message(SUCCESSFUL_EMAIL_WAS_SENT)
                        .body(linkedEmailEmailService.send(linkedEmail))
                        .build()
        );
    }
}