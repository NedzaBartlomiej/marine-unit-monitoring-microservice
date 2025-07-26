package pl.bartlomiej.apiservice.geocoding;

import pl.bartlomiej.apiservice.common.helper.Position;

import java.util.Optional;

public interface GeocodeService {
    Optional<Position> getAddressCoordinates(String address);
}
