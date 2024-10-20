package pl.bartlomiej.emailservice.service;

import pl.bartlomiej.emailservice.domain.Email;

public interface EmailService<T extends Email> {
    T send(T email);
}