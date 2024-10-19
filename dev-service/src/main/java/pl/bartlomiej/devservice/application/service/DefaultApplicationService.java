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

import java.util.List;

@Service
class DefaultApplicationService implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationService.class);
    private final ApplicationMongoRepository applicationMongoRepository;

    DefaultApplicationService(ApplicationMongoRepository applicationMongoRepository) {
        this.applicationMongoRepository = applicationMongoRepository;
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
        Application application = applicationMongoRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        application.setRequestStatus(requestStatus);
        applicationMongoRepository.save(application);

        // todo email service invocation
        log.info("Sending application request consideration consideration email, status: {}", requestStatus);

        return requestStatus;
    }
}