package pl.bartlomiej.emailservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.emailservice.domain.Email;
import pl.bartlomiej.emailservice.domain.StandardEmail;
import pl.bartlomiej.emailservice.service.EmailService;
import pl.bartlomiej.emailservice.service.EmailServiceProvider;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private final EmailServiceProvider emailServiceProvider;

    public EmailController(EmailServiceProvider emailServiceProvider) {
        this.emailServiceProvider = emailServiceProvider;
    }

    @PostMapping("/standard")
    public ResponseEntity<ResponseModel<Email>> sendStandardEmail(@RequestBody @Valid final StandardEmail standardEmail) {
        EmailService<StandardEmail> standardEmailService = emailServiceProvider.resolveEmailService(StandardEmail.class);
        return ResponseEntity.ok(
                new ResponseModel.Builder<Email>(HttpStatus.OK)
                        .message("A successful email was sent.")
                        .body(standardEmailService.send(standardEmail))
                        .build()
        );
    }
}