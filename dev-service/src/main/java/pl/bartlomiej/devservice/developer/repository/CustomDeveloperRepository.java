package pl.bartlomiej.devservice.developer.repository;

public interface CustomDeveloperRepository {
    void pushTrustedIpAddress(String id, String ipAddress);
}
