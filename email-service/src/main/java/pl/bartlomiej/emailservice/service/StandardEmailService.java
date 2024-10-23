package pl.bartlomiej.emailservice.service;

import pl.bartlomiej.emailservice.domain.StandardEmail;

@EmailServiceImpl
public class StandardEmailService implements EmailService<StandardEmail> {
    @Override
    public StandardEmail send(StandardEmail email) {
        return email;
    }
}
