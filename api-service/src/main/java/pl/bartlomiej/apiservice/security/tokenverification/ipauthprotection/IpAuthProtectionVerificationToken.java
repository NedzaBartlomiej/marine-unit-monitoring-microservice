package pl.bartlomiej.apiservice.security.tokenverification.ipauthprotection;

import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationToken;


public class IpAuthProtectionVerificationToken extends VerificationToken {

    private String ipAddress;

    public IpAuthProtectionVerificationToken() {
        super();
    }

    public IpAuthProtectionVerificationToken(String uid, long expirationTime, String type, String ipAddress) {
        super(uid, expirationTime, type);
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
