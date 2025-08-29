package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Date;

@Slf4j
@Service
public class DefaultSuspectLoginService implements SuspectLoginService {
    private final MongoSuspectLoginRepository mongoSuspectLoginRepository;
    private final IPinfo ipInfo;
    private final long cleaningTimeCountBeforeActual;

    public DefaultSuspectLoginService(MongoSuspectLoginRepository mongoSuspectLoginRepository,
                                      IPinfo ipInfo,
                                      @Value("${project-properties.times.in-ms.suspect-logins.cleaning-time-count-before-actual}")
                                      long cleaningTimeCountBeforeActual) {
        this.mongoSuspectLoginRepository = mongoSuspectLoginRepository;
        this.ipInfo = ipInfo;
        this.cleaningTimeCountBeforeActual = cleaningTimeCountBeforeActual;
    }

    @Override
    public SuspectLogin create(final String ipAddress, final String uid, final String loginServiceClientId) {
        log.info("Creating new suspect login report for user with id='{}' which logged to the service which 'loginServiceClientId'='{}'", uid, loginServiceClientId);
        if (ipAddress == null || ipAddress.isBlank() ||
                uid == null || uid.isBlank() ||
                loginServiceClientId == null || loginServiceClientId.isBlank()
        ) {
            throw new IllegalArgumentException("'ipAddress' or 'uid' or 'loginServiceClientId' is null or blank.");
        }

        try {
            log.trace("Obtaining additional info about IP address from the API for user with id='{}' which logged to the service which 'loginServiceClientId'='{}'", uid, loginServiceClientId);
            IPResponse ipResponse = ipInfo.lookupIP(ipAddress);

            SuspectLogin suspectLogin;
            if (ipResponse != null) {
                log.trace("Successfully obtained IP address additional info rom the API, creating suspect login report for user with id='{}' which logged to the service which 'loginServiceClientId'='{}'", uid, loginServiceClientId);
                suspectLogin = new SuspectLogin(
                        uid,
                        ipAddress,
                        loginServiceClientId,
                        ipResponse.getHostname(),
                        ipResponse.getCity(),
                        ipResponse.getRegion(),
                        ipResponse.getCountryName(),
                        ipResponse.getLocation()
                );
            } else {
                log.warn("The response from the IP lookup API is null, creating suspect login report without IP's additional info for user with id='{}' which logged to the service which 'loginServiceClientId'='{}'.", uid, loginServiceClientId);
                suspectLogin = new SuspectLogin(
                        uid,
                        ipAddress,
                        loginServiceClientId
                );
            }

            return this.mongoSuspectLoginRepository.save(suspectLogin);
        } catch (RateLimitedException e) {
            log.error("RateLimitedException occurred in the ip-info API requests, details: {}", e.getMessage());
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public void delete(final String id) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("'id' is null or blank.");
        mongoSuspectLoginRepository.deleteById(id);
    }

    /**
     * @param id  the identifier of the suspect login entry in the database; must not be {@code null}
     * @param uid the user identifier that must match the {@code uid} of the suspect login entry
     * @return the {@link SuspectLogin} object corresponding to the provided {@code id} and {@code uid}
     * @throws ErrorResponseException if the suspect login is not found ({@code NOT_FOUND})
     *                                or the {@code uid} does not match ({@code FORBIDDEN})
     */
    @Override
    public SuspectLogin get(final String id, final String uid) {
        if (id == null || id.isBlank() ||
                uid == null || uid.isBlank()
        ) throw new IllegalArgumentException("'id' or 'uid' is null or blank.");

        SuspectLogin suspectLogin = mongoSuspectLoginRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
        if (!suspectLogin.getUid().equals(uid)) {
            log.error("Suspect login does not belong to the requesting subject.");
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
        }
        return suspectLogin;
    }

    @Scheduled(initialDelay = 0, fixedDelayString = "${project-properties.scheduling-delays.in-ms.suspect-logins.cleaning}")
    public void clean() {
        log.debug("Cleaning dangling Suspect Login reports.");
        long timeBeforeMs = System.currentTimeMillis() - this.cleaningTimeCountBeforeActual;
        Date timeBeforeDate = new Date(timeBeforeMs);
        mongoSuspectLoginRepository.deleteAllByTimeBefore(timeBeforeDate);
    }
}