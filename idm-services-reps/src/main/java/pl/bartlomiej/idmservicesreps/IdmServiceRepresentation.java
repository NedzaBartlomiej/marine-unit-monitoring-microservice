package pl.bartlomiej.idmservicesreps;

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

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setResourceApiVersion(String resourceApiVersion) {
        this.resourceApiVersion = resourceApiVersion;
    }

    public void setIdmResourceIdentifier(String idmResourceIdentifier) {
        this.idmResourceIdentifier = idmResourceIdentifier;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}