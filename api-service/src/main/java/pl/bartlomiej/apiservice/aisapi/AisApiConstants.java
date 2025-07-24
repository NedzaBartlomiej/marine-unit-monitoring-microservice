package pl.bartlomiej.apiservice.aisapi;

public final class AisApiConstants {

    /**
     * JSON field name for longitude in response handled by {@link pl.bartlomiej.apiservice.aisapi.service.DefaultAisService#fetchShipsByMmsis}
     */
    public static final String LONGITUDE = "longitude";

    /**
     * JSON field name for latitude in response handled by {@link pl.bartlomiej.apiservice.aisapi.service.DefaultAisService#fetchShipsByMmsis}
     */
    public static final String LATITUDE = "latitude";

    private AisApiConstants() {
    }
}
