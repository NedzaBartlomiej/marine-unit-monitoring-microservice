package pl.bartlomiej.apiservice.point.activepoint.service;

import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;

import java.util.List;

public interface ActivePointService {

    List<String> getMmsis();

    void removeActivePoint(String mmsi);

    void addActivePoint(ActivePoint activePoint);

    boolean isPointActive(String mmsi);

    String getName(String mmsi);
}
