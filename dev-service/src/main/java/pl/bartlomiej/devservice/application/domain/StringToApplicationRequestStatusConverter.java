package pl.bartlomiej.devservice.application.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pl.bartlomiej.devservice.common.exception.apiexception.InvalidApplicationRequestStatusException;

@Component
public class StringToApplicationRequestStatusConverter implements Converter<String, ApplicationRequestStatus> {
    @Override
    public ApplicationRequestStatus convert(@NonNull String source) {
        try {
            return ApplicationRequestStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidApplicationRequestStatusException();
        }
    }
}
