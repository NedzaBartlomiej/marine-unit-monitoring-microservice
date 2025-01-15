package pl.bartlomiej.loginservices;

public class IdmServiceRepresentation {

    private String hostname;
    private int port;
    private String resourceApiVersion;
    private String idmResourceIdentifier;
    private String clientId;

    public IdmServiceRepresentation(String hostname, int port, String resourceApiVersion, String idmResourceIdentifier, String clientId) {
        this.hostname = hostname;
        this.port = port;
        this.resourceApiVersion = resourceApiVersion;
        this.idmResourceIdentifier = idmResourceIdentifier;
        this.clientId = clientId;
    }

    public IdmServiceRepresentation() {
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getResourceApiVersion() {
        return resourceApiVersion;
    }

    public String getIdmResourceIdentifier() {
        return idmResourceIdentifier;
    }

    public String getClientId() {
        return clientId;
    }
}