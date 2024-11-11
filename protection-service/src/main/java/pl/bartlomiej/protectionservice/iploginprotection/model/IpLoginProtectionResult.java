package pl.bartlomiej.protectionservice.iploginprotection.model;

public enum IpLoginProtectionResult {
    TRUSTED_IP("Trusted IP address, nothing action has been performed."),
    UNTRUSTED_IP("Untrusted IP address, securing actions have been performed.");

    private final String detailsMessage;

    IpLoginProtectionResult(String detailsMessage) {
        this.detailsMessage = detailsMessage;
    }

    public String getDetailsMessage() {
        return detailsMessage;
    }
}
