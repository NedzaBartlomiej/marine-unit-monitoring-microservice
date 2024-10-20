package pl.bartlomiej.emailservice.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.bartlomiej.emailservice.domain.Email;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

// todo - refactor
@Component
public class EmailServiceProvider {

    private final Map<Class<? extends Email>, EmailService<? extends Email>> emailServices = new HashMap<>();

    public EmailServiceProvider(ApplicationContext context) {
        String[] emailServicesNames = context.getBeanNamesForAnnotation(EmailServiceImpl.class);
        for (String emailServiceName : emailServicesNames) {

            var emailService = (EmailService<?>) context.getBean(emailServiceName);

            // should be - EmailService implementation's, implemented interfaces
            var implementedInterfaces = (ParameterizedType[]) emailService.getClass().getGenericInterfaces();

            // should be - Email.class
            Class<? extends Email>[] interfacesArgsTypes = (Class<? extends Email>[]) implementedInterfaces[0].getActualTypeArguments();

            this.emailServices.put(interfacesArgsTypes[0], emailService);
        }
    }

    public <T extends Email> EmailService<T> resolveEmailService(Class<T> emailType) {
        return (EmailService<T>) this.emailServices.get(emailType);
    }
}
