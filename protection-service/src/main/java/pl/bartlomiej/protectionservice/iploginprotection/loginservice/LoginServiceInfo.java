package pl.bartlomiej.protectionservice.iploginprotection.loginservice;

/**
 * This class representing particular service with the log in functionality: <p>
 * service that performs log in feature,
 * service to which user can log in,
 * service which have a default registration role <p>
 * For example: api-service -> hostname = api-service, defaultRole = DEF_API_USER
 *
 * @param hostname    hostname is equal to service name f.e. api-service -> api-service (docker ecosystem)
 * @param defaultRole default service-client's role in an auth server represented as String
 **/
public record LoginServiceInfo(String hostname, String defaultRole) {
}