package pl.bartlomiej.apiservice.common.helper;

import java.time.Duration;

public class CacheControlHelper {
    public static Duration getSafeMaxAge(Duration maxAge, Duration safetyOffset) {
        Duration safeMaxAge = maxAge.minus(safetyOffset);
        return safeMaxAge.isNegative() ? Duration.ZERO : safeMaxAge;
    }
}
