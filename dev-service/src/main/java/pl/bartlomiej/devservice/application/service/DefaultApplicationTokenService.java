package pl.bartlomiej.devservice.application.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.repository.ApplicationMongoRepository;

import java.util.UUID;

@Service
class DefaultApplicationTokenService implements ApplicationTokenService {

    private final ApplicationMongoRepository applicationMongoRepository;

    public DefaultApplicationTokenService(ApplicationMongoRepository applicationMongoRepository) {
        this.applicationMongoRepository = applicationMongoRepository;
    }

    @Override
    public String generateToken() {
        String appToken;
        do {
            appToken = UUID.randomUUID().toString();
        } while (applicationMongoRepository.existsByAppToken(appToken));
        return appToken;
    }

    @Override
    public Boolean checkToken(final String appToken) {
        Application application = applicationMongoRepository.findByAppToken(appToken);
        return application != null && !application.getIsBlocked();
    }

    @Override
    public String replaceCurrentAppToken(String id) {
        Application application = applicationMongoRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        String token = this.generateToken();
        application.setAppToken(token);

        applicationMongoRepository.save(application);
        return token;
    }
}