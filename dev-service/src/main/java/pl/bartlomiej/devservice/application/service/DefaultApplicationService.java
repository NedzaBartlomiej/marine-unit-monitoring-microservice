package pl.bartlomiej.devservice.application.service;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
        } catch (DuplicateKeyException e) { // todo - test is CONFLICT status working
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

    //    @Transactional - todo only allowed in the replicaSet approach (create one element replicaSet - maybe do the same in the api-service)
    @Override // todo - add validation if existing status equals to provided status
    public ApplicationRequestStatus considerAppRequest(final String id, final ApplicationRequestStatus requestStatus, final String details) {
        return switch (requestStatus) {
            case ACCEPTED -> this.acceptAppRequest(id).getRequestStatus();
            case REJECTED -> this.rejectAppRequest(id, details).getRequestStatus();
            default -> throw new InvalidApplicationRequestStatusException();
        };
    }

    private Application rejectAppRequest(final String id, final String details) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.REJECTED);
        AppDeveloperEntity developer = this.developerService.getEntity(application.getDevId());

        this.sendConsiderationEmail("Your application request has been rejected ❌. Details:\n"
                        + details,
                developer.getEmail());

        return applicationMongoRepository.save(application);
    }

    private Application acceptAppRequest(final String id) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.ACCEPTED);
        application.setAppToken(applicationTokenService.generateToken());
        AppDeveloperEntity developer = this.developerService.getEntity(application.getDevId());

        this.sendConsiderationEmail("Your application request has been accepted ✅, you can read more info in your developer panel.",
                developer.getEmail());

        return applicationMongoRepository.save(application);
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
        application.setRequestStatus(requestStatus);
        return application;
    }
}