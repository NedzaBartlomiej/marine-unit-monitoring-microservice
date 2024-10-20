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
import pl.bartlomiej.devservice.common.exception.InvalidApplicationRequestStatusException;

import java.util.List;

@Service
class DefaultApplicationService implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationService.class);
    private final ApplicationMongoRepository applicationMongoRepository;
    private final ApplicationTokenService applicationTokenService;

    DefaultApplicationService(ApplicationMongoRepository applicationMongoRepository, ApplicationTokenService applicationTokenService) {
        this.applicationMongoRepository = applicationMongoRepository;
        this.applicationTokenService = applicationTokenService;
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

    //    @Transactional - todo only allowed in the replicaSet approach (create one element replicaSet - maybe do the same in the api-service)
    @Override
    public ApplicationRequestStatus considerAppRequest(final String id, final ApplicationRequestStatus requestStatus) {
        return switch (requestStatus) {
            case ACCEPTED -> this.acceptAppRequest(id).getRequestStatus();
            case REJECTED -> this.rejectAppRequest(id).getRequestStatus();
            default -> throw new InvalidApplicationRequestStatusException();
        };
    }

    private Application rejectAppRequest(final String id) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.REJECTED);

        // send email

        return applicationMongoRepository.save(application);
    }

    private Application acceptAppRequest(final String id) {
        Application application = this.updateRequestStatus(id, ApplicationRequestStatus.ACCEPTED);
        application.setAppToken(applicationTokenService.generateToken());

        // send email

        return applicationMongoRepository.save(application);
    }

    private Application updateRequestStatus(final String id, final ApplicationRequestStatus requestStatus) {
        Application application = applicationMongoRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
        application.setRequestStatus(requestStatus);
        return application;
    }
}