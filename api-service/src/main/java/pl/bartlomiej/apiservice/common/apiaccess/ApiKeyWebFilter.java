package pl.bartlomiej.apiservice.common.apiaccess;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiej.mumcommons.coreutils.model.response.ResponseModel;

import java.io.IOException;

@Slf4j
@Component
public class ApiKeyWebFilter extends OncePerRequestFilter {

    private final DevServiceHttpService devServiceHttpService;

    public ApiKeyWebFilter(DevServiceHttpService devServiceHttpService) {
        this.devServiceHttpService = devServiceHttpService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Filtering the request for correctness of API KEY (x-api-key header).");
        String extractedApiKey = this.extractApiKey(request);
        if (!this.checkApiKey(extractedApiKey)) {
            log.debug("API key validation failed for key: {}", extractedApiKey);
            throw new AuthenticationCredentialsNotFoundException("API key is invalid.");
        }
        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        log.debug("Extracting 'x-api-key' header from the request.");
        String xApiKey = request.getHeader("x-api-key");
        if (xApiKey == null || xApiKey.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("'x-api-key' header is null or blank.");
        }
        log.debug("Extracted 'x-api-key' header successfully, returning.");
        return xApiKey;
    }

    private boolean checkApiKey(String apiKey) {
        log.debug("Checking if API key is valid.");
        ResponseEntity<ResponseModel<Boolean>> tokenValidationResponse = this.devServiceHttpService.checkToken(apiKey);
        if (tokenValidationResponse == null ||
                tokenValidationResponse.getBody() == null ||
                tokenValidationResponse.getStatusCode().isError()
        ) {
            log.error("The response from the validating service is invalid. The response: {}", tokenValidationResponse);
            throw new AuthenticationServiceException("Something went wrong during checking the API key.");
        }
        return tokenValidationResponse.getBody().getBody();
    }
}