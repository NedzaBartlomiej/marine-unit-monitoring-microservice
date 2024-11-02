package pl.bartlomiej.devservice.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.ApplicationRequestStatus;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;
import pl.bartlomiej.devservice.application.repository.ApplicationMongoRepository;
import pl.bartlomiej.devservice.common.exception.apiexception.InvalidApplicationRequestStatusException;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.service.DeveloperService;
import pl.bartlomiej.mummicroservicecommons.constants.TokenConstants;
import pl.bartlomiej.mummicroservicecommons.emailintegration.external.EmailHttpService;
import pl.bartlomiej.mummicroservicecommons.emailintegration.external.model.StandardEmail;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;

import java.util.List;

@Service
class DefaultApplicationService implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationService.class);
    public static final String CONSIDERATION_EMAIL_TITLE = "Application request consideration 📩";
    private final ApplicationMongoRepository applicationMongoRepository;
    private final ApplicationTokenService applicationTokenService;
    private final EmailHttpService emailHttpService;
    private final DeveloperService developerService;
    private final KeycloakService keycloakService;

    DefaultApplicationService(ApplicationMongoRepository applicationMongoRepository, ApplicationTokenService applicationTokenService, EmailHttpService emailHttpService, DeveloperService developerService, KeycloakService keycloakService) {
        this.applicationMongoRepository = applicationMongoRepository;
        this.applicationTokenService = applicationTokenService;
        this.emailHttpService = emailHttpService;
        this.developerService = developerService;
        this.keycloakService = keycloakService;
    }

    @Override
    public Application create(final ApplicationRequestDto applicationRequestDto, final String devId) {
        var application = new Application(
                devId,
                applicationRequestDto.requestDesc(),
                applicationRequestDto.name()
        );

        try {
            return applicationMongoRepository.save(application);
        } catch (DuplicateKeyException e) {
            throw new ErrorResponseException(HttpStatus.CONFLICT, e);
        }
    }

    @Override
    public List<Application> getApplications(final ApplicationRequestStatus requestStatus) {
        return (requestStatus == null)
                ? applicationMongoRepository.findAll()
                : applicationMongoRepository.findAllByRequestStatus(requestStatus);
    }

    @Override
    public List<Application> getApplications(final String devId) {
        return applicationMongoRepository.findAllByDevId(devId);
    }

    @Transactional
    @Override
    public ApplicationRequestStatus considerAppRequest(final String id, final ApplicationRequestStatus requestStatus, final String details) {
        return switch (requestStatus) {
            case ACCEPTED -> this.acceptAppRequest(id, details).getRequestStatus();
            case REJECTED -> this.rejectAppRequest(id, details).getRequestStatus();
            default -> throw new InvalidApplicationRequestStatusException();
        };
    }

    private Application rejectAppRequest(final String id, final String details) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.REJECTED);
        AppDeveloperEntity developer = this.developerService.getEntity(application.getDevId());

        applicationMongoRepository.save(application);

        this.sendConsiderationEmail(this.buildConsiderationEmailMessage("Your application " +
                        "request has been rejected ❌.", details),
                developer.getEmail());

        return application;
    }

    private Application acceptAppRequest(final String id, final String details) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.ACCEPTED);
        application.setAppToken(applicationTokenService.generateToken());
        AppDeveloperEntity developer = this.developerService.getEntity(application.getDevId());

        applicationMongoRepository.save(application);

        this.sendConsiderationEmail(this.buildConsiderationEmailMessage(
                        "Your application request " +
                                "has been accepted ✅, " +
                                "you can read more info in your developer panel.", details),
                developer.getEmail());

        return application;
    }

    private void sendConsiderationEmail(String message, String developerEmail) {
        log.info("Sending consideration email.");
        emailHttpService.sendStandardEmail(
                TokenConstants.BEARER_PREFIX + keycloakService.getAccessToken(),
                new StandardEmail(developerEmail,
                        CONSIDERATION_EMAIL_TITLE,
                        message
                ));
    }

    private Application updateRequestStatus(final String id, final ApplicationRequestStatus requestStatus) {
        Application application = applicationMongoRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
        if (application.getRequestStatus().equals(requestStatus)) {
            log.info("The requested status is equal to the current status, cancel the update.");
            throw new ErrorResponseException(HttpStatus.NOT_MODIFIED);
        }

        application.setRequestStatus(requestStatus);
        return application;
    }

    private String buildConsiderationEmailMessage(final String message, final String details) {
        StringBuilder messageBuilder = new StringBuilder(message);
        if (details != null) {
            messageBuilder.append(" Details: ").append(details);
        }
        return messageBuilder.toString();
    }
}