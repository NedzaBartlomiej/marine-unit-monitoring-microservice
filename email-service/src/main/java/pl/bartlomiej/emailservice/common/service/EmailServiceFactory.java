package pl.bartlomiej.emailservice.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.emailintegration.external.model.Email;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceFactory.class);
    private final Map<Class<? extends Email>, EmailService<? extends Email>> emailServices = new HashMap<>();

    public EmailServiceFactory(final ApplicationContext context) {
        log.debug("Initializing an email services map.");
        String[] emailServicesNames = context.getBeanNamesForAnnotation(EmailServiceImpl.class);
        for (String emailServiceName : emailServicesNames) {
            var emailService = (EmailService<? extends Email>) context.getBean(emailServiceName);
            ParameterizedType emailServiceInterface = this.getAbstractEmailServiceSuperclassType(emailService);
            Class<? extends Email> emailClassType = this.extractEmailClassType(emailServiceInterface);

            this.emailServices.put(emailClassType, emailService);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Email> EmailService<T> resolveEmailService(final Class<T> emailType) {
        log.debug("Resolving an email service by Email.class type.");
        EmailService<? extends Email> emailService = this.emailServices.get(emailType);
        if (emailService == null) {
            throw new IllegalArgumentException("No EmailService found for emailType: " + emailType.getName());
        }
        return (EmailService<T>) emailService;
    }

    private ParameterizedType getAbstractEmailServiceSuperclassType(final EmailService<? extends Email> emailService) {
        log.debug("Acquisition of extended AbstractEmailService of the email service implementation superclass.");
        Type genericSuperclass = emailService.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            log.warn("Invalid superclass found, couldn't cast to ParametrizedType.class for: {}", genericSuperclass.getTypeName());
            if (parameterizedType.getRawType() == AbstractEmailService.class) {
                return parameterizedType;
            }
        }
        throw new IllegalStateException("No AbstractEmailService<T extends Email> superclass found for: " + emailService.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private <T extends Email> Class<T> extractEmailClassType(final ParameterizedType emailServiceInterface) {
        log.debug("Extracting Email.class type from the implemented email service interface.");
        Type[] interfaceArgsTypes = emailServiceInterface.getActualTypeArguments();
        for (Type interfaceArgType : interfaceArgsTypes) {
            if (interfaceArgType instanceof Class<?> clazz) {
                log.warn("Invalid interface argument type, couldn't cast to Class.class for: {}", interfaceArgType.getTypeName());
                if (Email.class.isAssignableFrom(clazz)) {
                    return (Class<T>) clazz;
                }
            }
        }
        throw new IllegalStateException("No Class<? extends Email> found for " + emailServiceInterface.getTypeName() + " in it's actual type arguments.");
    }
}