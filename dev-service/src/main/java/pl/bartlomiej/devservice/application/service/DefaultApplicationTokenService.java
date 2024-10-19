package pl.bartlomiej.devservice.application.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.repository.ApplicationMongoRepository;

@Service
class DefaultApplicationTokenService implements ApplicationTokenService {

    private final ApplicationMongoRepository applicationMongoRepository;

    public DefaultApplicationTokenService(ApplicationMongoRepository applicationMongoRepository) {
        this.applicationMongoRepository = applicationMongoRepository;
    }

    @Override
    public Boolean checkToken(final String appToken) {
        Application application = applicationMongoRepository.findByAppToken(appToken);
        return application != null && !application.getIsBlocked();
    }
}