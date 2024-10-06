package pl.bartlomiej.apiservice.user.domain;

import pl.bartlomiej.apiservice.common.util.CommonFields;

public final class UserConstants implements CommonFields {

    public static final String TRACKED_SHIPS = "trackedShips";
    public static final String OPEN_IDS = "openIds";
    public static final String TRUSTED_IP_ADDRESSES = "trustedIpAddresses";
    public static final String IS_TWO_FACTOR_AUTH_ENABLED = "isTwoFactorAuthEnabled";

    private UserConstants() {
    }
}
