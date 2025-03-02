package pl.bartlomiej.apiservice.common.apiaccess;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyWebFilter extends OncePerRequestFilter {

    private final DevServiceHttpService devServiceHttpService;

    public ApiKeyWebFilter(DevServiceHttpService devServiceHttpService) {
        this.devServiceHttpService = devServiceHttpService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String apiKey = this.extractApiKey(request);
        var checkResponse = devServiceHttpService.checkToken(apiKey);
        if (checkResponse.getBody() == null) {
            throw new AuthenticationServiceException("Api token validation internal error.");
        }
        if (!checkResponse.getBody().getBody()) {
            throw new AuthenticationCredentialsNotFoundException("x-api-key header token is invalid.");
        }
        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        String xApiKey = request.getHeader("x-api-key");
        if (xApiKey == null || xApiKey.isBlank())
            throw new AuthenticationCredentialsNotFoundException("x-api-key header is null.");
        return xApiKey;
    }
}