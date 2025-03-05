package pl.bartlomiej.apiservice.geocoding.service;

import pl.bartlomiej.apiservice.geocoding.Position;

public interface GeocodeService {
    Position getAddressCoordinates(String address);
}
