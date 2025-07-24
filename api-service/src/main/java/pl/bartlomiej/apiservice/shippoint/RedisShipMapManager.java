package pl.bartlomiej.apiservice.shippoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class RedisShipMapManager implements ShipMapManager {

    private static final String SHIP_POINTS_RH = "shipPoints";
    private final AisApiShipPointAdapter aisApiShipPointAdapter;
    private final RedisTemplate<String, ShipPoint> redisTemplate;
    private final HashOperations<String, String, ShipPoint> hashOperations;
    private volatile LocalDateTime lastRefreshed;
    private final long shipPointMapRefreshmentDelay;

    RedisShipMapManager(AisApiShipPointAdapter aisApiShipPointAdapter,
                        @Qualifier("shipPointRedisTemplate") RedisTemplate<String, ShipPoint> redisTemplate,
                        @Value("${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment.refreshing}") long shipPointMapRefreshmentDelay) {
        this.aisApiShipPointAdapter = aisApiShipPointAdapter;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.shipPointMapRefreshmentDelay = shipPointMapRefreshmentDelay;
    }

    @Override
    public Optional<List<ShipPoint>> getActiveShipPoints() {
        List<ShipPoint> shipPoints = hashOperations.values(SHIP_POINTS_RH);
        return shipPoints.isEmpty()
                ? Optional.empty()
                : Optional.of(shipPoints);
    }

    @Override
    public Optional<List<String>> getActiveShipMmsis() {
        List<String> shipPointsMmsis = hashOperations.values(SHIP_POINTS_RH).stream()
                .map(ShipPoint::mmsi)
                .toList();
        return shipPointsMmsis.isEmpty()
                ? Optional.empty()
                : Optional.of(shipPointsMmsis);
    }

    @Override
    public boolean isShipPointActive(String mmsi) {
        return hashOperations.hasKey(SHIP_POINTS_RH, mmsi);
    }

    // To keep in mind: hashOperations.get(H key, Object hashKey)
    // also returns null if `H key` doesn't exist.
    @Override
    public Optional<String> getShipPointName(String mmsi) {
        return Optional.ofNullable(hashOperations.get(SHIP_POINTS_RH, mmsi))
                .map(ShipPoint::name);
    }

    @Override
    public void refreshMap() {
        aisApiShipPointAdapter.getLatestShipPoints()
                .ifPresentOrElse(shipPoints -> {
                            log.info("Refreshing ShipPoints map.");
                            Map<String, ShipPoint> shipPointMap = shipPoints.stream()
                                    .collect(Collectors.toMap(
                                                    ShipPoint::mmsi,
                                                    Function.identity()
                                            )
                                    );
                            redisTemplate.delete(SHIP_POINTS_RH);
                            hashOperations.putAll(SHIP_POINTS_RH, shipPointMap);
                            this.lastRefreshed = LocalDateTime.now();
                        },
                        () -> log.info("Lack of the latest ships, the state of the ShipPoints map remains unchanged.")
                );
    }

    @Override
    public LocalDateTime getLastRefreshed() {
        return this.lastRefreshed;
    }

    @Override
    public long getShipPointMapRefreshmentDelay() {
        return this.shipPointMapRefreshmentDelay;
    }

    @Scheduled(initialDelayString = "${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment.initialDelay}", fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment.refreshing}")
    public void scheduledRefreshMap() {
        this.refreshMap();
    }
}
