package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

@Slf4j
@Service
public class DefaultSuspectLoginService implements SuspectLoginService {
    private final MongoSuspectLoginRepository mongoSuspectLoginRepository;
    private final IPinfo ipInfo;

    public DefaultSuspectLoginService(MongoSuspectLoginRepository mongoSuspectLoginRepository, IPinfo ipInfo) {
        this.mongoSuspectLoginRepository = mongoSuspectLoginRepository;
        this.ipInfo = ipInfo;
    }

    @Override
    public SuspectLogin create(final String ipAddress, final String uid, final String loginServiceClientId) {
        log.debug("Initializing new suspect login.");
        try {
            IPResponse ipResponse = ipInfo.lookupIP(ipAddress);
            return this.mongoSuspectLoginRepository.save(
                    new SuspectLogin(
                            uid,
                            ipAddress,
                            loginServiceClientId,
                            ipResponse.getHostname(),
                            ipResponse.getCity(),
                            ipResponse.getRegion(),
                            ipResponse.getCountryName(),
                            ipResponse.getLocation()
                    )
            );
        } catch (RateLimitedException e) {
            log.error("Rate limited exception occurred in the ip-info API requests, details: {}", e.getMessage());
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public void delete(final String id) {
        if (!mongoSuspectLoginRepository.existsById(id)) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND);
        }
        mongoSuspectLoginRepository.deleteById(id);
    }

    @Override
    public SuspectLogin get(final String id, final String uid) {
        SuspectLogin suspectLogin = mongoSuspectLoginRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
        if (!suspectLogin.getUid().equals(uid)) {
            log.error("Suspect login does not belong to the requesting subject user.");
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
        }
        return suspectLogin;
    }
}