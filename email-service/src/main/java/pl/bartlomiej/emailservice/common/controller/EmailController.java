package pl.bartlomiej.emailservice.common.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.emailservice.common.service.EmailService;
import pl.bartlomiej.mumcommons.coreutils.model.response.ResponseModel;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;
import pl.bartlomiej.mumcommons.emailintegration.external.model.StandardEmail;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private static final String SUCCESSFUL_EMAIL_SENT = "A successful email has been sent.";
    private final EmailService<StandardEmail> standardEmailService;
    private final EmailService<LinkedEmail> linkedEmailService;

    public EmailController(EmailService<StandardEmail> standardEmailService, EmailService<LinkedEmail> linkedEmailService) {

        this.standardEmailService = standardEmailService;
        this.linkedEmailService = linkedEmailService;
    }

    @PostMapping("/standard")
    public ResponseEntity<ResponseModel<StandardEmail>> sendStandardEmail(@RequestBody @Valid final StandardEmail standardEmail) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<StandardEmail>(HttpStatus.OK, true)
                        .message(SUCCESSFUL_EMAIL_SENT)
                        .body(standardEmailService.send(standardEmail))
                        .build()
        );
    }

    @PostMapping("/linked")
    public ResponseEntity<ResponseModel<LinkedEmail>> sendLinkedEmail(@RequestBody @Valid final LinkedEmail linkedEmail) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<LinkedEmail>(HttpStatus.OK, true)
                        .message(SUCCESSFUL_EMAIL_SENT)
                        .body(linkedEmailService.send(linkedEmail))
                        .build()
        );
    }
}