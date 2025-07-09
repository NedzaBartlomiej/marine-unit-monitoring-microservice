package pl.bartlomiej.apiservice.shippoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;

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

    RedisShipMapManager(AisApiShipPointAdapter aisApiShipPointAdapter, RedisTemplate<String, ShipPoint> redisTemplate) {
        this.aisApiShipPointAdapter = aisApiShipPointAdapter;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public List<ShipPoint> getActiveShipPoints() {
        return hashOperations.values(SHIP_POINTS_RH);
    }

    @Override
    public List<String> getActiveShipMmsis() {
        return hashOperations.values(SHIP_POINTS_RH).stream()
                .map(ShipPoint::mmsi)
                .toList();
    }

    @Override
    public boolean isShipPointActive(String mmsi) {
        return hashOperations.hasKey(SHIP_POINTS_RH, mmsi);
    }

    // To keep in mind: hashOperations.get(H key, Object hashKey)
    // also returns null if `H key` doesn't exist.
    @Override
    public String getShipPointName(String mmsi) {
        return Optional.ofNullable(hashOperations.get(SHIP_POINTS_RH, mmsi))
                .map(ShipPoint::destinationName)
                .orElseThrow(() ->
                        new MmsiConflictException(MmsiConflictException.Message.INVALID_SHIP.message)
                );
    }

    // todo: shipPoints is not being created - nothing appears in Redis after this method invocation
    @Override
    public void refreshMap() {
        log.info("Refreshing Redis ShipPoints Map.");
        Map<String, ShipPoint> shipPointMap = aisApiShipPointAdapter.getShipPoints().stream()
                .collect(Collectors.toMap(
                        ShipPoint::mmsi,
                        Function.identity()
                ));
        redisTemplate.delete(SHIP_POINTS_RH);
        hashOperations.putAll(SHIP_POINTS_RH, shipPointMap);
        this.lastRefreshed = LocalDateTime.now();
    }

    @Override
    public LocalDateTime lastRefreshed() {
        return this.lastRefreshed;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void refreshOnApplicationStart() {
        this.refreshMap();
    }

    @Scheduled(initialDelay = 360000, fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment}")
    public void scheduledRefreshMap() {
        this.refreshMap();
    }
}
