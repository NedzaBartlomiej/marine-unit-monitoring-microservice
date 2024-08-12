package pl.bartlomiej.apiservice.geocoding.service;

import pl.bartlomiej.apiservice.geocoding.Position;
import reactor.core.publisher.Flux;

public interface GeocodeService {
    Flux<Position> getAddressCoordinates(String address);
}
