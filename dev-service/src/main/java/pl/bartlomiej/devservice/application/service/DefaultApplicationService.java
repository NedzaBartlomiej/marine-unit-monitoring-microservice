package pl.bartlomiej.devservice.application.service;

import com.mongodb.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;
import pl.bartlomiej.devservice.application.repository.ApplicationMongoRepository;

@Service
class DefaultApplicationService implements ApplicationService {

    private final ApplicationMongoRepository applicationMongoRepository;

    DefaultApplicationService(ApplicationMongoRepository applicationMongoRepository) {
        this.applicationMongoRepository = applicationMongoRepository;
    }

    @Override
    public Application create(ApplicationRequestDto applicationRequestDto, String devId) {
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
}
