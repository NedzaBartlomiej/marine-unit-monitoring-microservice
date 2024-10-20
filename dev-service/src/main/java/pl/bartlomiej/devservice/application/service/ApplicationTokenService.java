package pl.bartlomiej.devservice.application.service;

public interface ApplicationTokenService {
    String generateToken();

    Boolean checkToken(String appToken);

    String replaceCurrentAppToken(String id);
}