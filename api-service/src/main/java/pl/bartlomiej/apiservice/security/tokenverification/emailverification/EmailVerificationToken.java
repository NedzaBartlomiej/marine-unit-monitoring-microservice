package pl.bartlomiej.apiservice.security.tokenverification.emailverification;

import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationToken;

public class EmailVerificationToken extends VerificationToken {

    public EmailVerificationToken() {
        super();
    }

    public EmailVerificationToken(String uid, long expirationTime, String type) {
        super(uid, expirationTime, type);
    }
}
