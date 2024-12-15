package pl.bartlomiej.emailservice.common.service;

import pl.bartlomiej.mumcommons.emailintegration.external.model.Email;

public interface EmailService<T extends Email> {
    T send(T email);
}